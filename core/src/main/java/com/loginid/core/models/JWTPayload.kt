package com.loginid.core.models

import com.squareup.moshi.JsonClass
import java.util.Date

/**
 * A decoded JSON Web Token (JWT) payload containing the expiration time.
 */
@JsonClass(generateAdapter = true)
internal data class JWTPayload(
    /** Expiration time of the JWT, in seconds since epoch. */
    val exp: Long = (Date().time / 1000) + 300
)
