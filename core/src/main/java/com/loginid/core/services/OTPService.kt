package com.loginid.core.services

import com.loginid.client.api.AuthApi
import com.loginid.client.model.AuthCode
import com.loginid.client.model.AuthCodeRequestSMSRequestBody
import com.loginid.client.model.AuthCodeVerifyRequestBody
import com.loginid.client.model.JWT
import com.loginid.core.interfaces.OTPAPI
import com.loginid.core.models.LoginIDConfig

/**
 * A service that implements the [OTPAPI] protocol.
 *
 * @param config The LoginID configuration.
 */
class OTPService(private val config: LoginIDConfig) : OTPAPI {
    /**
     * The API client for authentication-related operations.
     */
    val authApi = AuthApi(config.getBaseUrl())

    /**
     * Requests an OTP code for an authenticated user.
     *
     * @param authorization The JWT authorization header.
     * @return An [AuthCode] object containing the OTP code and its expiration.
     */
    override suspend fun authCodeRequest(authorization: String?): AuthCode {
        return authApi.authAuthCodeRequest(authorization = authorization)
    }

    /**
     * Verifies an OTP code and returns a JWT access token.
     *
     * @param request The verification request body.
     * @return A [JWT] object upon successful verification.
     */
    override suspend fun authCodeVerify(request: AuthCodeVerifyRequestBody): JWT {
        return authApi.authAuthCodeVerify(authCodeVerifyRequestBody = request)
    }

    /**
     * Requests an OTP code to be sent via email.
     *
     * @param request The request body containing user information.
     */
    override suspend fun authCodeRequestEmail(request: AuthCodeRequestSMSRequestBody) {
        return authApi.authAuthCodeRequestEmail(authCodeRequestSMSRequestBody = request)
    }

    /**
     * Requests an OTP code to be sent via SMS.
     *
     * @param request The request body containing user information.
     */
    override suspend fun authCodeRequestSMS(request: AuthCodeRequestSMSRequestBody) {
        return authApi.authAuthCodeRequestEmail(authCodeRequestSMSRequestBody = request)
    }
}
