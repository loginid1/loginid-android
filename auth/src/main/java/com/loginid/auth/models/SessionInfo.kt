package com.loginid.auth.models

import com.loginid.core.models.LoginIDJWTAccess

/**
 * Provides information about the current authenticated session.
 *
 * @property username The authenticated user's username.
 * @property id The subject identifier for the authenticated user.
 * @property rpId Relying party ID used typically used for passkey authentication to confirm the user has signed in under the specific domain.
 */
data class SessionInfo(
    val username: String,
    val id: String,
    val rpId: String
) {
    /**
     * Initializes session information from a decoded LoginID access token.
     *
     * @param loginIdToken The decoded access token.
     */
    constructor(loginIdToken: LoginIDJWTAccess) : this(
        username = loginIdToken.username,
        id = loginIdToken.sub,
        rpId = loginIdToken.rpId
    )
}
