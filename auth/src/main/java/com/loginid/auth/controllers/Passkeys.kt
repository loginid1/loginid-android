package com.loginid.auth.controllers

import android.app.Activity
import android.view.View
import com.loginid.auth.extensions.fromFallback
import com.loginid.auth.extensions.fromJWT
import com.loginid.auth.extensions.mergeFallbackMethods
import com.loginid.auth.models.AuthResult
import com.loginid.auth.models.AuthenticateWithPasskeyOptions
import com.loginid.auth.models.ConfirmTransactionOptions
import com.loginid.auth.models.CreatePasskeyOptions
import com.loginid.auth.models.TxConfirmResult
import com.loginid.core.errors.LoginIDError
import com.loginid.core.extensions.toAuthenticatorAssertionResponse
import com.loginid.core.extensions.toCreationResult
import com.loginid.core.extensions.toJSON
import com.loginid.core.interfaces.PasskeyAPI
import com.loginid.core.interfaces.PublicKeyManaging
import com.loginid.core.models.LoginIDConfig
import com.loginid.core.stores.DeviceStore
import com.loginid.core.stores.SessionManager
import com.loginid.core.utils.Defaults
import com.loginid.core.utils.DeviceUtils
import com.loginid.core.utils.TrustID
import io.loginid.client.model.Application
import io.loginid.client.model.AuthCompleteRequestBody
import io.loginid.client.model.AuthInit
import io.loginid.client.model.AuthInitRequestBody
import io.loginid.client.model.RegCompleteRequestBody
import io.loginid.client.model.RegInitRequestBody
import io.loginid.client.model.TxCompleteRequestBody
import io.loginid.client.model.TxInitRequestBody

internal class Passkeys(
    private val config: LoginIDConfig,
    private val device: DeviceStore,
    private val session: SessionManager,
    private val trustId: TrustID,
    private val passkeyApi: PasskeyAPI,
    private val publicKeyManager: PublicKeyManaging
) {
    suspend fun createPasskey(
        activity: Activity,
        username: String,
        options: CreatePasskeyOptions? = null
    ): AuthResult {
        val appId = config.getAppId()
        val deviceInfo = Defaults.deviceInfo(activity, device, options?.deviceId)
        val trustItems = Defaults.trustItems(
            config = config,
            store = trustId,
            username = username
        )
        val user = Defaults.user(
            displayName = options?.displayName,
            username = username,
            usernameType = options?.usernameType
        )

        val regInitRequestBody = RegInitRequestBody(
            app = Application(appId),
            deviceInfo = deviceInfo,
            user = user,
            trustItems = trustItems
        )

        val userAgent = DeviceUtils.getUserAgent()
        val authzToken = session.getAuthzToken(options?.authzToken)

        val initRes = passkeyApi.regInit(
            request = regInitRequestBody,
            userAgent = userAgent,
            authorization = authzToken,
        )

        return invokePasskeyApi(initRes.session) {
            val credential = publicKeyManager.create(
                activity = activity,
                publicKey = initRes.registrationRequestOptions.toJSON()
            )

            val regCompleteRequestBody = RegCompleteRequestBody(
                creationResult = credential.toCreationResult(),
                session = initRes.session,
                passkeyName = options?.passkeyName,
            )

            val response = passkeyApi.regComplete(regCompleteRequestBody)

            session.setAccessToken(response.jwtAccess)
            device.setDeviceId(response.deviceId)

            AuthResult().fromJWT(response)
        }
    }

    suspend fun authenticateWithPasskey(
        activity: Activity,
        username: String,
        usernameAnchorView: View? = null,
        options: AuthenticateWithPasskeyOptions? = null
    ): AuthResult {
        val appId = config.getAppId()
        val deviceInfo = Defaults.deviceInfo(activity, device)
        val trustItems = Defaults.trustItems(
            config = config,
            store = trustId,
            username = username
        )
        val user = Defaults.userLogin(
            username = username,
            usernameType = options?.usernameType
        )

        val authInitRequestBody = AuthInitRequestBody(
            app = Application(appId),
            deviceInfo = deviceInfo,
            user = user,
            trustItems = trustItems
        )

        val userAgent = DeviceUtils.getUserAgent()

        val initRes = passkeyApi.authInit(
            request = authInitRequestBody,
            userAgent = userAgent
        )

        when (initRes.action) {
            AuthInit.Action.PROCEED -> {
                val publicKey = initRes.assertionOptions
                    ?: throw LoginIDError(
                        "passkey_authentication_failed",
                        "Assertion options not found or invalid"
                    )

                return invokePasskeyApi(initRes.session) {
                    val credential = publicKeyManager.get(
                        activity = activity,
                        publicKey = publicKey.toJSON(),
                        usernameAnchorView = usernameAnchorView,
                    )

                    val authCompleteRequestBody = AuthCompleteRequestBody(
                        assertionResult = credential.toAuthenticatorAssertionResponse(),
                        session = initRes.session
                    )

                    val response = passkeyApi.authComplete(authCompleteRequestBody)

                    session.setAccessToken(response.jwtAccess)
                    device.setDeviceId(response.deviceId)

                    AuthResult().fromJWT(response)
                }
            }
            AuthInit.Action.CROSS_AUTH, AuthInit.Action.FALLBACK -> {
                val fallbackOptions = initRes.mergeFallbackMethods()
                return AuthResult().fromFallback(fallbackOptions)
            }
        }
    }

    suspend fun confirmTransaction(
        activity: Activity,
        username: String,
        txPayload: String,
        options: ConfirmTransactionOptions?
    ): TxConfirmResult {
        val nonce = Defaults.nonce(options?.nonce)
        val txType = Defaults.txType(options?.txType)

        val txInitRequestBody = TxInitRequestBody(
            nonce = nonce,
            txPayload = txPayload,
            txType = txType,
            username = username
        )

        val initRes = passkeyApi.txInit(txInitRequestBody)
        val publicKey = initRes.assertionOptions

        return invokePasskeyApi(initRes.session) {
            val credential = publicKeyManager.get(
                activity = activity,
                publicKey = publicKey.toJSON(),
                usernameAnchorView = null
            )

            val txCompleteRequestBody = TxCompleteRequestBody(
                authenticatorData = credential.response.authenticatorData,
                clientData = credential.response.clientDataJSON,
                signature = credential.response.signature,
                keyHandle = credential.id,
                session = initRes.session,
            )

            val result = passkeyApi.txComplete(txCompleteRequestBody)

            TxConfirmResult(result)
        }
    }

    private suspend fun <T> invokePasskeyApi(
        session: String,
        fn: suspend () -> T
    ): T {
        return try {
            fn()
        } catch (e: Exception) {
            // TODO: clientEvents.submitASAuthorizationErrorEvent
            throw e
        }
    }
}
