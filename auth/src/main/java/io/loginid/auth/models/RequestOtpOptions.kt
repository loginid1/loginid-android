package io.loginid.auth.models

/**
 * Options for requesting a one-time password (OTP).
 *
 * @param authzToken An optional authorization token for the request. If not provided, the SDK will use a stored token if available.
 */
data class RequestOtpOptions(
    val authzToken: String? = null
)
