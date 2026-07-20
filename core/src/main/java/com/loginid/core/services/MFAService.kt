package com.loginid.core.services

import com.loginid.core.interfaces.MFAAPI
import com.loginid.core.models.LoginIDConfig
import io.loginid.client.api.MfaApi
import io.loginid.client.model.Mfa
import io.loginid.client.model.MfaBeginRequestBody
import io.loginid.client.model.MfaNext
import io.loginid.client.model.MfaPasskeyAuthRequestBody
import io.loginid.client.model.MfaPasskeyRegRequestBody
import io.loginid.client.model.MfaPayloadUpdateRequestBody
import io.loginid.client.model.MfaThirdPartyAuthVerifyRequestBody

/**
 * A service that implements the [MFAAPI] for MFA operations.
 *
 * @param config The LoginID configuration.
 */
class MFAService(config: LoginIDConfig) : MFAAPI {
    private val mfaApi = MfaApi(config.getBaseUrl())

    /**
     * Begins the MFA flow for a given user.
     *
     * @param request The request body for beginning MFA.
     * @param userAgent The user agent of the client.
     * @return A [MfaNext] object containing the next steps in the MFA flow.
     */
    override suspend fun mfaBegin(request: MfaBeginRequestBody, userAgent: String?): MfaNext {
        return mfaApi.mfaMfaBegin(mfaBeginRequestBody = request, userAgent = userAgent ?: "")
    }

    /**
     * Updates the payload for a transaction confirmation.
     *
     * @param request The request body for updating the payload.
     * @param authorization The authorization token.
     * @return A [MfaNext] object with updated flow details.
     */
    override suspend fun mfaPayloadUpdate(request: MfaPayloadUpdateRequestBody, authorization: String?): MfaNext {
        return mfaApi.mfaMfaPayloadUpdate(mfaPayloadUpdateRequestBody = request, authorization = authorization)
    }

    /**
     * Registers a new passkey within an MFA flow.
     *
     * @param request The request body for passkey registration.
     * @param authorization The authorization token.
     * @return An [Mfa] object representing the completed MFA step.
     */
    override suspend fun mfaPasskeyReg(request: MfaPasskeyRegRequestBody, authorization: String?): Mfa {
        return mfaApi.mfaMfaPasskeyReg(mfaPasskeyRegRequestBody = request, authorization = authorization)
    }

    /**
     * Confirms a transaction with a passkey within an MFA flow.
     *
     * @param request The request body for transaction confirmation.
     * @param authorization The authorization token.
     * @return An [Mfa] object representing the completed MFA step.
     */
    override suspend fun mfaPasskeyTx(request: MfaPasskeyAuthRequestBody, authorization: String?): Mfa {
        return mfaApi.mfaMfaPasskeyTx(mfaPasskeyAuthRequestBody = request, authorization = authorization)
    }

    /**
     * Authenticates with a passkey within an MFA flow.
     *
     * @param request The request body for passkey authentication.
     * @param authorization The authorization token.
     * @return An [Mfa] object representing the completed MFA step.
     */
    override suspend fun mfaPasskeyAuth(request: MfaPasskeyAuthRequestBody, authorization: String?): Mfa {
        return mfaApi.mfaMfaPasskeyAuth(mfaPasskeyAuthRequestBody = request, authorization = authorization)
    }

    /**
     * Verifies a third-party authentication token within an MFA flow.
     *
     * @param request The request body for third-party token verification.
     * @param authorization The authorization token.
     * @return An [Mfa] object representing the completed MFA step.
     */
    override suspend fun mfaThirdPartyAuthVerify(request: MfaThirdPartyAuthVerifyRequestBody, authorization: String?): Mfa {
        return mfaApi.mfaMfaThirdPartyAuthVerify(mfaThirdPartyAuthVerifyRequestBody = request, authorization = authorization)
    }
}
