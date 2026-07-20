package io.loginid.core.extensions

import com.squareup.moshi.Moshi
import io.loginid.client.model.PublicKeyCredentialRequestOptions

/**
 * Converts the [PublicKeyCredentialRequestOptions] object to its JSON string representation.
 *
 * @return The JSON string representation of the object.
 * @throws Exception if the object cannot be parsed into a JSON string.
 */
fun PublicKeyCredentialRequestOptions.toJSON(): String {
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter(PublicKeyCredentialRequestOptions::class.java)
    return jsonAdapter.toJson(this) ?: throw Exception("Unable to parse response")
}
