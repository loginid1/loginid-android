package com.loginid.core.interfaces

import io.loginid.client.model.Mfa
import io.loginid.client.model.MfaBeginRequestBody
import io.loginid.client.model.MfaNext
import io.loginid.client.model.MfaPasskeyAuthRequestBody
import io.loginid.client.model.MfaPasskeyRegRequestBody
import io.loginid.client.model.MfaPayloadUpdateRequestBody
import io.loginid.client.model.MfaThirdPartyAuthVerifyRequestBody

/**
 * An interface for the Multi-Factor Authentication (MFA) API.
 */
interface MFAAPI {
    /**
     * Begins the MFA flow for a given user.
     *
     * @param request The request body for beginning MFA.
     * @param userAgent The user agent of the client.
     * @return A [MfaNext] object containing the next steps in the MFA flow.
     */
    suspend fun mfaBegin(request: MfaBeginRequestBody, userAgent: String?): MfaNext

    /**
     * Updates the payload for a transaction confirmation.
     *
     * @param request The request body for updating the payload.
     * @param authorization The authorization token.
     * @return A [MfaNext] object with updated flow details.
     */
    suspend fun mfaPayloadUpdate(request: MfaPayloadUpdateRequestBody, authorization: String?): MfaNext

    /**
     * Registers a new passkey within an MFA flow.
     *
     * @param request The request body for passkey registration.
     * @param authorization The authorization token.
     * @return An [Mfa] object representing the completed MFA step.
     */
    suspend fun mfaPasskeyReg(request: MfaPasskeyRegRequestBody, authorization: String?): Mfa

    /**
     * Confirms a transaction with a passkey within an MFA flow.
     *
     * @param request The request body for transaction confirmation.
     * @param authorization The authorization token.
     * @return An [Mfa] object representing the completed MFA step.
     */
    suspend fun mfaPasskeyTx(request: MfaPasskeyAuthRequestBody, authorization: String?): Mfa

    /**
     * Authenticates with a passkey within an MFA flow.
     *
     * @param request The request body for passkey authentication.
     * @param authorization The authorization token.
     * @return An [Mfa] object representing the completed MFA step.
     */
    suspend fun mfaPasskeyAuth(request: MfaPasskeyAuthRequestBody, authorization: String?): Mfa

    /**
     * Verifies a third-party authentication token within an MFA flow.
     *
     * @param request The request body for third-party token verification.
     * @param authorization The authorization token.
     * @return An [Mfa] object representing the completed MFA step.
     */
    suspend fun mfaThirdPartyAuthVerify(request: MfaThirdPartyAuthVerifyRequestBody, authorization: String?): Mfa
}
