package io.loginid.auth.controllers

import io.loginid.auth.extensions.fromJWT
import io.loginid.auth.models.AuthResult
import io.loginid.auth.models.OTPResult
import io.loginid.auth.models.RequestAndSendOtpOptions
import io.loginid.auth.models.RequestOtpOptions
import io.loginid.auth.models.ValidateOtpOptions
import io.loginid.core.enums.MessageMethod
import io.loginid.core.interfaces.OTPAPI
import io.loginid.core.stores.SessionManager
import io.loginid.core.utils.Defaults
import io.loginid.client.model.AuthCodeRequestSMSRequestBody
import io.loginid.client.model.AuthCodeVerifyRequestBody

internal class OTP(
    private val session: SessionManager,
    private val otpApi: OTPAPI
) {

    suspend fun requestOtp(options: RequestOtpOptions?): OTPResult {
        val authzToken = session.getAuthzToken(options?.authzToken)
        val result = otpApi.authCodeRequest(authzToken)
        return OTPResult(result)
    }

    suspend fun validateOtp(
        username: String,
        otp: String,
        options: ValidateOtpOptions?
    ): AuthResult {
        val user = Defaults.userLogin(username, options?.usernameType)
        val requestBody = AuthCodeVerifyRequestBody(otp, user)
        val result = otpApi.authCodeVerify(requestBody)

        session.setAccessToken(result.jwtAccess)

        return AuthResult().fromJWT(result)
    }

    suspend fun requestAndSendOtp(
        username: String,
        method: MessageMethod,
        options: RequestAndSendOtpOptions?
    ) {
        val user = Defaults.userLogin(username, options?.usernameType)
        val requestBody = AuthCodeRequestSMSRequestBody(user)

        when (method) {
            MessageMethod.EMAIL -> otpApi.authCodeRequestEmail(requestBody)
            MessageMethod.SMS -> otpApi.authCodeRequestSMS(requestBody)
        }
    }
}
