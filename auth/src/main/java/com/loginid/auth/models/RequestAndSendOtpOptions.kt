package com.loginid.auth.models

import com.loginid.core.enums.UsernameType

/**
 * Options for requesting and sending a one-time password (OTP).
 *
 * @param usernameType The type of username validation to be used. Defaults to `.OTHER`.
 */
data class RequestAndSendOtpOptions(
    val usernameType: UsernameType = UsernameType.OTHER
)
