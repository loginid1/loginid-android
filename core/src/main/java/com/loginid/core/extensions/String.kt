package com.loginid.core.extensions

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
