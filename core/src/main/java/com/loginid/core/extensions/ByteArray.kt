package com.loginid.core.extensions

import java.util.Base64

/**
 * Encodes this ByteArray as a Base64URL string (RFC 7515-compliant).
 *
 * @return A Base64URL-encoded string without padding.
 */
internal fun ByteArray.base64URLEncode(): String {
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(this)
}