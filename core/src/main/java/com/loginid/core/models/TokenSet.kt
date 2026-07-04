package com.loginid.core.models

import com.squareup.moshi.JsonClass

/**
 * A collection of authentication tokens returned after a successful login.
 *
 * @property idToken An authorization token (JWT) confirming successful authentication. Optional.
 * @property accessToken An authorization token (JWT) for API access. Optional.
 * @property refreshToken A token used to obtain a new access token. Optional.
 * @property payloadSignature A signature for the payload. Optional.
 */
@JsonClass(generateAdapter = true)
data class TokenSet(
    val idToken: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val payloadSignature: String? = null
)
