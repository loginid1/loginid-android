package io.loginid.core.extensions

import java.util.Base64

/**
 * Decodes a Base64URL-encoded string into a ByteArray.
 *
 * @return The decoded [ByteArray], or `null` if the string is not valid Base64URL.
 */
internal fun String.base64URLDecode(): ByteArray? {
    return try {
        // The URL decoder handles strings with or without padding.
        Base64.getUrlDecoder().decode(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

/**
 * Decodes a JWT payload segment or a raw Base64URL-encoded string into a ByteArray.
 *
 * If the string appears to be a JWT (`header.payload.signature`), the method
 * extracts and decodes only the payload segment. Otherwise, it decodes the

 * entire string as Base64URL.
 *
 * @return The decoded [ByteArray], or `null` if the string is not valid Base64URL.
 */
fun String.decodeJWTOrPayloadSegment(): ByteArray? {
    val segment = this.split(".").getOrNull(1) ?: this
    return segment.base64URLDecode()
}
