package com.loginid.auth.extensions

import com.loginid.auth.models.AuthResult
import com.loginid.auth.models.FallbackMethodsResult
import io.loginid.client.model.JWT

/**
 * Populates an [AuthResult] instance from a [JWT] response object.
 * This is a convenience function to convert a successful JWT response into an authentication result.
 *
 * @param response The [JWT] object containing authentication data.
 * @return An [AuthResult] object with authentication details.
 */
internal fun AuthResult.fromJWT(response: JWT): AuthResult {
    return AuthResult(
        isAuthenticated = true,
        token = response.jwtAccess,
        userId = response.userId,
        passkeyId = response.passkeyId,
        deviceId = response.deviceId,
    )
}

/**
 * Populates an [AuthResult] instance from a [FallbackMethodsResult].
 * This is a convenience function to convert a fallback result into an authentication result.
 *
 * @param result The [FallbackMethodsResult] object containing fallback options.
 * @return An [AuthResult] object with fallback details.
 */
internal fun AuthResult.fromFallback(result: FallbackMethodsResult): AuthResult {
    return AuthResult(
        isAuthenticated = false,
        isFallback = result.isEmpty().not(),
        fallbackOptions = result,
    )
}
