package com.loginid.auth.models

typealias FallbackMethodsResult = List<String>

/**
 * The result of an authentication attempt using either a passkey
 * or a one-time password (OTP).
 */
data class AuthResult(
    /**
     * A Boolean value indicating whether the user was successfully authenticated.
     *
     * If `false`, inspect [fallbackOptions] to determine the available
     * alternative authentication methods.
     */
    val isAuthenticated: Boolean = false,
    /**
     * A Boolean value indicating whether the authentication process has resulted in a fallback.
     * When `true`, [isAuthenticated] will be `false` and [fallbackOptions] will contain
     * the available alternative authentication methods.
     */
    val isFallback: Boolean = false,
    /**
     * The authentication methods that can be used as a fallback when passkey
     * authentication is not available or recommended.
     */
    val fallbackOptions: FallbackMethodsResult? = null,
    /**
     * A short-lived authorization token that can be used to access protected
     * resources for the authenticated user, such as listing, renaming, or
     * deleting passkeys.
     *
     * This value is `null` when authentication falls back to an alternative
     * authentication method.
     */
    val token: String? = null,
    /**
     * The unique identifier of the authenticated user.
     *
     * This value is `null` when authentication was not completed.
     */
    val userId: String? = null,
    /**
     * The identifier of the passkey used to authenticate the user.
     *
     * This value is `null` when authentication did not use a passkey or was
     * not completed.
     */
    val passkeyId: String? = null,
    /**
     * An identifier for the device that participated in the authentication
     * process.
     *
     * This identifier can be used to determine whether future authentication
     * methods are supported on the current device.
     */
    val deviceId: String? = null,
)

