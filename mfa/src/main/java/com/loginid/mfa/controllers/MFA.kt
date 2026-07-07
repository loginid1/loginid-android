package com.loginid.mfa.controllers

import com.loginid.client.model.Mfa
import com.loginid.client.model.MfaActionAction
import com.loginid.client.model.MfaBeginRequestBody
import com.loginid.client.model.MfaNext
import com.loginid.client.model.MfaPasskeyAuthRequestBody
import com.loginid.client.model.MfaPasskeyRegRequestBody
import com.loginid.client.model.MfaPayloadUpdateRequestBody
import com.loginid.client.model.MfaThirdPartyAuthVerifyRequestBody
import com.loginid.client.model.PublicKeyCredentialCreationOptions
import com.loginid.client.model.PublicKeyCredentialRequestOptions
import com.loginid.core.errors.LoginIDError
import com.loginid.core.extensions.toAuthenticatorAssertionResponse
import com.loginid.core.extensions.toCreationResult
import com.loginid.core.errors.LoginIDException
import com.loginid.core.interfaces.MFAAPI
import com.loginid.core.interfaces.PublicKeyManaging
import com.loginid.core.models.LoginIDConfig
import com.loginid.core.stores.DeviceStore
import com.loginid.core.stores.SessionManager
import com.loginid.core.utils.Defaults
import com.loginid.core.utils.DeviceUtils
import com.loginid.core.utils.TrustID
import com.loginid.mfa.enums.ActionName
import com.loginid.mfa.models.BeginFlowOptions
import com.loginid.mfa.models.MFAData
import com.loginid.mfa.models.MFAInfo
import com.loginid.mfa.models.MFASessionResult
import com.loginid.mfa.models.PasskeyOptions
import com.loginid.mfa.models.PerformActionOptions
import com.loginid.mfa.stores.MFAStore
import com.loginid.mfa.utils.MFAValidator
import com.loginid.mfa.utils.TrustTokenFinder
import com.squareup.moshi.Moshi
import org.openapitools.client.infrastructure.ClientException

internal class MFA(
    private val config: LoginIDConfig,
    private val device: DeviceStore,
    private val session: SessionManager,
    private val store: MFAStore,
    private val trustId: TrustID,
    private val mfaApi: MFAAPI,
    private val publicKeyManager: PublicKeyManaging
) {
    /**
     * Initiates the pre-authentication process for Multi-Factor Authentication (MFA).
     *
     * Begins an MFA session and persists session details to secure storage.
     *
     * @param username The username of the user initiating MFA.
     * @param options Optional parameters for initiating MFA. Pass `null` to use defaults.
     * @return An `MFASessionResult` describing the current MFA session state and next actions.
     * @throws com.loginid.core.errors.LoginIDError if the request fails or the response is invalid.
     */
    suspend fun beginFlow(
        username: String,
        options: BeginFlowOptions?
    ): MFASessionResult {
        store.clearTrustSet()

        val deviceInfo = Defaults.deviceInfo(
            context = config.getContext(),
            store = device,
            deviceId = options?.deviceId
        )

        val userAgent = DeviceUtils.getUserAgent()

        val trustItems = Defaults.trustItems(
            config = config,
            store = trustId,
            txPayload = options?.txPayload,
            merchantTrustId = options?.merchantTrustId,
            username = username
        )

        val user = Defaults.userMfa(
            displayName = options?.displayName,
            username = username,
            usernameType = options?.usernameType
        )

        val request = MfaBeginRequestBody(
            deviceInfo = deviceInfo,
            payload = options?.txPayload,
            traceId = options?.traceId,
            trustItems = trustItems,
            user = user
        )

        val mfaNextResult = mfaApi.mfaBegin(request, userAgent)

        val trustSet = TrustTokenFinder.findTrustTokens(mfaNextResult)
        store.storeTrustSet(trustSet)

        val info = MFAInfo(mfaNextResult, username)

        session.logout()
        store.storeMFAInfo(info)

        return MFASessionResult.from(info, null, trustSet)
    }

    /**
     * Performs a Multi-Factor Authentication (MFA) action using the specified factor.
     *
     * Supports passkeys, OTP (email/SMS), and external authentication. Validates the
     * provided options, processes the step, calls the corresponding MFA API, and updates
     * the MFA session details upon success.
     *
     * **Important:**
     * - **Passkeys:** Uses WebAuthn for authentication or registration.
     * - **External authentication:** Provide the authorization code in `options.payload`.
     * - **OTP Request (email/SMS):** Sends an OTP to the user's contact. If `options.payload`
     *   contains a contact, it is used; otherwise, the primary contact on record is used.
     * - **OTP Verify (email/SMS):** Verifies the OTP code provided in `options.payload`.
     *
     * @param action The MFA factor to perform (e.g., `MFAActionName.PASSKEY_REG`).
     * @param options The session/payload options for the action. Pass `null` to resolve defaults when possible.
     * @return An updated `MFASessionResult` reflecting the new MFA state and next actions.
     * @throws LoginIDError if validation fails, the payload is missing/invalid, or the API call fails.
     */
    suspend fun performAction(
        action: ActionName,
        options: PerformActionOptions?
    ): MFASessionResult {
        val clientAction = action.clientEnum
        val info = store.getMFAInfo()
        val values = MFAValidator.actionValidator(action = clientAction, info = info, options = options)

        if (clientAction == MfaActionAction.Name.PASSKEY_COLON_TX && !options?.txPayload.isNullOrEmpty()) {
            val rest = options.withoutTxPayload()
            val mfaPayloadUpdateRequestBody = MfaPayloadUpdateRequestBody(payload = options.txPayload)
            val mfaNextResult = mfaApi.mfaPayloadUpdate(
                request = mfaPayloadUpdateRequestBody,
                authorization = values.session
            )
            val username = info?.username
            val newInfo = MFAInfo(result = mfaNextResult, username = username)

            store.storeMFAInfo(newInfo)

            return performAction(action, rest)
        }

        return when (clientAction) {
            MfaActionAction.Name.PASSKEY_COLON_REG, MfaActionAction.Name.PASSKEY_COLON_AUTH, MfaActionAction.Name.PASSKEY_COLON_TX -> {
                when (val passkeyOptions = MFAValidator.validatePasskeyPayload(values.payload)) {
                    is PasskeyOptions.Creation -> {
                        val creationOptions = passkeyOptions.options
                        invokeMfaApi(username = info?.username) {
                            val activity = options?.activity
                                ?: throw LoginIDError("not_found", "An activity is required for passkey operations.")

                            val finalCreationOptions = if (!options.displayName.isNullOrEmpty()) {
                                creationOptions.copy(
                                    user = creationOptions.user.copy(
                                        displayName = options.displayName,
                                        name = options.displayName
                                    )
                                )
                            } else {
                                creationOptions
                            }

                            val moshi = Moshi.Builder().build()
                            val creationOptionsJson = moshi.adapter(PublicKeyCredentialCreationOptions::class.java).toJson(finalCreationOptions)

                            val credential = publicKeyManager.create(activity, creationOptionsJson)

                            val requestBody = MfaPasskeyRegRequestBody(creationResult = credential.toCreationResult())

                            mfaApi.mfaPasskeyReg(
                                request = requestBody,
                                authorization = values.session
                            )
                        }
                    }
                    is PasskeyOptions.Request -> {
                        val requestOptions = passkeyOptions.options
                        invokeMfaApi(username = info?.username) {
                            val activity = options?.activity
                                ?: throw LoginIDError("not_found", "An activity is required for passkey operations.")

                            val moshi = Moshi.Builder().build()
                            val requestOptionsJson = moshi.adapter(PublicKeyCredentialRequestOptions::class.java).toJson(requestOptions)

                            val credential = publicKeyManager.get(
                                activity,
                                requestOptionsJson,
                                options.usernameAnchorView
                            )

                            val requestBody = MfaPasskeyAuthRequestBody(assertionResult = credential.toAuthenticatorAssertionResponse())

                            when (clientAction) {
                                MfaActionAction.Name.PASSKEY_COLON_TX -> mfaApi.mfaPasskeyTx(
                                    request = requestBody,
                                    authorization = values.session
                                )
                                MfaActionAction.Name.PASSKEY_COLON_AUTH -> mfaApi.mfaPasskeyAuth(
                                    request = requestBody,
                                    authorization = values.session
                                )
                                else -> throw LoginIDError(
                                    msgCode = "not_supported",
                                    msg = "MFA factor $clientAction is not supported in the current MFA flow."
                                )
                            }
                        }
                    }
                }
            }
            MfaActionAction.Name.EXTERNAL -> {
                invokeMfaApi(username = info?.username) {
                    val requestBody = MfaThirdPartyAuthVerifyRequestBody(token = values.payload)
                    mfaApi.mfaThirdPartyAuthVerify(
                        request = requestBody,
                        authorization = values.session
                    )
                }
            }
            else -> {
                throw LoginIDError(
                    msgCode = "not_supported",
                    msg = "MFA factor $clientAction is not supported in the current MFA flow."
                )
            }
        }
    }

    private suspend fun invokeMfaApi(
        username: String?,
        fn: suspend () -> Mfa
    ): MFASessionResult {
        val trustSet = store.getTrustSet()
        return try {
            val performActionResult = fn()
            val info = store.getMFAInfo()
            val successInfo = MFAInfo(username = username, flow = info?.flow)
            val mfaData = MFAData(performActionResult)

            store.storeMFAInfo(successInfo)
            device.setDeviceId(performActionResult.deviceId)
            session.setTokenSet(mfaData.tokenSet)

            MFASessionResult.from(successInfo, mfaData, trustSet)
        } catch (e: ClientException) {
            if (e.statusCode == 401) {
                val mfaNextResult = LoginIDException.parseClientErrorBody(e, MfaNext::class)
                if (mfaNextResult?.session != null) {
                    val info = MFAInfo(mfaNextResult, username)
                    store.storeMFAInfo(info)
                    return MFASessionResult.from(info, null, trustSet)
                }
            }
            throw LoginIDException.parseError(e)
        } catch (e: Exception) {
            // TODO: client events API
            throw e
        }
    }
}
