package com.loginid.core.interfaces

import com.loginid.client.model.AuthCode
import com.loginid.client.model.AuthCodeRequestSMSRequestBody
import com.loginid.client.model.AuthCodeVerifyRequestBody
import com.loginid.client.model.JWT

/**
 * An interface for handling One-Time Password (OTP) authentication flows.
 */
interface OTPAPI {
    /**
     * Requests an OTP code for an authenticated user.
     * @param authorization The JWT authorization header.
     * @return An [AuthCode] object containing the OTP code and its expiration.
     * @throws com.loginid.core.errors.LoginIDError if the request fails.
     */
    suspend fun authCodeRequest(authorization: String?): AuthCode

    /**
     * Verifies an OTP code and returns a JWT access token.
     * @param request The verification request body.
     * @return A [JWT] object upon successful verification.
     * @throws com.loginid.core.errors.LoginIDError if verification fails.
     */
    suspend fun authCodeVerify(request: AuthCodeVerifyRequestBody): JWT

    /**
     * Requests an OTP code to be sent via email.
     * @param request The request body containing user information.
     * @throws com.loginid.core.errors.LoginIDError if the request fails.
     */
    suspend fun authCodeRequestEmail(request: AuthCodeRequestSMSRequestBody)

    /**
     * Requests an OTP code to be sent via SMS.
     * @param request The request body containing user information.
     * @throws com.loginid.core.errors.LoginIDError if the request fails.
     */
    suspend fun authCodeRequestSMS(request: AuthCodeRequestSMSRequestBody)
}
