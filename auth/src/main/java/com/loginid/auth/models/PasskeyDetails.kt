package com.loginid.auth.models

import io.loginid.client.model.Passkey

/**
 * Represents a passkey credential stored and managed within the LoginID SDK.
 *
 * Provides information about the passkey’s provider, creation date,
 * usage history, and associated device information.
 *
 * @property aaguid AAGUID (Authenticator Attestation GUID) of the passkey provider.
 * @property id The ID of the passkey.
 * @property credentialId The original raw credential ID of the given passkey when it was created via WebAuthn.
 * @property name Human-readable name assigned to the passkey.
 * @property providerName Name of the passkey provider (e.g., Apple, Google).
 * @property createdAt Timestamp when the passkey was created, in RFC3339 format.
 * @property lastUsedAt Timestamp when the passkey was last used, in RFC3339 format.
 * @property credentialSynced Indicates whether the credential is available across multiple devices (synced).
 * @property lastUsedFromDevice Information about the device from which the passkey was last used.
 */
data class PasskeyDetails(
    val aaguid: String,
    val id: String,
    val credentialId: String?,
    val name: String,
    val providerName: String?,
    val createdAt: String,
    val lastUsedAt: String?,
    val credentialSynced: Boolean,
    val lastUsedFromDevice: DeviceDetails?
) {
    /**
     * Initializes a new [PasskeyDetails] instance from a given [Passkey].
     *
     * @param passkey A [Passkey] value, typically received from the backend API.
     */
    internal constructor(passkey: Passkey) : this(
        aaguid = passkey.aaguid,
        id = passkey.id,
        credentialId = passkey.credentialId,
        name = passkey.name,
        providerName = passkey.providerName,
        createdAt = passkey.createdAt,
        lastUsedAt = passkey.lastUsedAt,
        credentialSynced = passkey.credentialSynced ?: false,
        lastUsedFromDevice = passkey.lastUsedFromDevice?.let { DeviceDetails(it) }
    )

    companion object {
        /**
         * Converts a list of [Passkey] objects to a list of [PasskeyDetails] objects.
         * @param passkeys A list of [Passkey] objects from the core module.
         * @return A list of [PasskeyDetails] objects.
         */
        fun fromArray(passkeys: List<Passkey>): List<PasskeyDetails> {
            return passkeys.map { PasskeyDetails(it) }
        }
    }
}
