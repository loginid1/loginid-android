package com.loginid.auth.controllers

import com.loginid.auth.extensions.fromJWT
import com.loginid.auth.models.AuthResult
import com.loginid.auth.models.OTPResult
import com.loginid.auth.models.RequestAndSendOtpOptions
import com.loginid.auth.models.RequestOtpOptions
import com.loginid.auth.models.ValidateOtpOptions
import com.loginid.client.model.AuthCodeRequestSMSRequestBody
import com.loginid.client.model.AuthCodeVerifyRequestBody
import com.loginid.core.enums.MessageMethod
import com.loginid.core.interfaces.OTPAPI
import com.loginid.core.stores.SessionManager
import com.loginid.core.utils.Defaults

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
