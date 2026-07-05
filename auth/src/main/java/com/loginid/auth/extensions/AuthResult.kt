package com.loginid.auth.extensions

import com.loginid.auth.models.AuthResult
import com.loginid.client.model.JWT

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
