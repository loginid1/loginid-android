package io.loginid.core.models

import com.squareup.moshi.JsonClass

/**
 * Represents the header of a JSON Web Token (JWT).
 */
@JsonClass(generateAdapter = true)
internal data class JWTHeader(
    /** The signing algorithm used for the JWT. */
    val alg: String,
    /** The JSON Web Key (JWK) associated with the JWT. */
    val jwk: JWK,
)
