package io.loginid.auth.models

typealias FallbackMethodsResult = List<String>

/**
 * Represents the result of an authentication attempt using either a passkey or a one-time password (OTP).
 *
 * This structure provides the outcome of the attempt and, when applicable, guidance on alternative methods.
 *
 * @property isAuthenticated Indicates whether the user was successfully authenticated. If false, inspect `[fallbackOptions]` for available alternatives.
 * @property isFallback Indicates whether the flow has transitioned to a fallback method. When true, `[isAuthenticated]` is false and `[fallbackOptions]` will be populated.
 * @property fallbackOptions The authentication methods that can be used as a fallback when passkey authentication is not available or recommended.
 * @property token A short-lived authorization token for accessing protected resources (e.g., list/rename/delete passkeys). Null when the result is a fallback.
 * @property userId The unique identifier of the authenticated user, or null if authentication was not completed.
 * @property passkeyId The identifier of the passkey used to authenticate the user, or null if a passkey was not used or authentication was not completed.
 * @property deviceId An identifier for the device that participated in the authentication process. Can be used to infer supported methods on this device.
 */
data class AuthResult(
    val isAuthenticated: Boolean = false,
    val isFallback: Boolean = false,
    val fallbackOptions: FallbackMethodsResult? = null,
    val token: String? = null,
    val userId: String? = null,
    val passkeyId: String? = null,
    val deviceId: String? = null,
)

