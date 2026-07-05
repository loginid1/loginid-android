package com.loginid.auth.models

import com.loginid.core.enums.UsernameType

/**
 * Options for validating a one-time password (OTP).
 *
 * @param usernameType The type of username validation to be used. Defaults to `.OTHER`.
 */
data class ValidateOtpOptions(
    val usernameType: UsernameType = UsernameType.OTHER
)
