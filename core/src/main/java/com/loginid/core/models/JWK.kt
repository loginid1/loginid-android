package com.loginid.core.models

import com.squareup.moshi.JsonClass

/**
 * Represents a JSON Web Key (JWK).
 */
@JsonClass(generateAdapter = true)
data class JWK(
    /** The key type parameter identifies the cryptographic algorithm family used with the key. */
    val kty: String,
    /** The curve parameter identifies the cryptographic curve used with the key. */
    val crv: String,
    /** The x-coordinate for the Elliptic Curve point. */
    val x: String,
    /** The y-coordinate for the Elliptic Curve point. */
    val y: String,
)
