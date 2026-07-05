package com.loginid.core.extensions

import com.loginid.client.model.PublicKeyCredentialCreationOptions
import com.squareup.moshi.Moshi

/**
 * Converts the [PublicKeyCredentialCreationOptions] object to its JSON string representation.
 *
 * @return The JSON string representation of the object.
 * @throws Exception if the object cannot be parsed into a JSON string.
 */
fun PublicKeyCredentialCreationOptions.toJSON(): String {
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter(PublicKeyCredentialCreationOptions::class.java)
    return jsonAdapter.toJson(this) ?: throw Exception("Unable to parse response")
}
