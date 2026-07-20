package io.loginid.core.models

import com.squareup.moshi.JsonClass

/**
 * Represents a stored Trust ID record.
 *
 * @property id A unique identifier for the Trust ID record.
 * @property appId The application identifier associated with this record.
 * @property username The username associated with this record.
 * @property keyAlias The alias for the key pair stored in the Android KeyStore.
 * @property lastUsedAt The timestamp when this record was last used.
 */
@JsonClass(generateAdapter = true)
internal data class TrustIDRecord(
    val id: String,
    val appId: String,
    val username: String,
    val keyAlias: String,
    var lastUsedAt: Long
)
