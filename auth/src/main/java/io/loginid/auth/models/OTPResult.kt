package io.loginid.auth.models

import io.loginid.client.model.AuthCode

/**
 * The result of a successful one-time password (OTP) request.
 *
 * @param code The generated one-time password.
 * @param expiresAt The expiration timestamp of the code in RFC3339 format.
 */
data class OTPResult(
    val code: String,
    val expiresAt: String
) {
    /**
     * Initializes an `OTPResult` from an `AuthCode` API response.
     * @param result The `AuthCode` object from the API.
     */
    internal constructor(result: AuthCode) : this(
        code = result.code,
        expiresAt = result.expiresAt
    )
}
