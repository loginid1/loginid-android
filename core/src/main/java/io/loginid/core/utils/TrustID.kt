package io.loginid.core.utils

import io.loginid.core.errors.LoginIDError
import io.loginid.core.extensions.base64URLEncode
import io.loginid.core.interfaces.KeyStoreManaging
import io.loginid.core.interfaces.Storage
import io.loginid.core.interfaces.StorageKey
import io.loginid.core.interfaces.TrustIDManaging
import io.loginid.core.models.JWTHeader
import io.loginid.core.models.JWTPayload
import io.loginid.core.models.LoginIDConfig
import io.loginid.core.models.TrustIDRecord
import com.squareup.moshi.Moshi
import java.util.UUID

/**
 * A helper type that manages the lifecycle of a wallet identity using a cryptographic key stored in the Keychain.
 *
 * @param LoginIDConfig Configuration to obtain the app ID.
 * @param storage An instance of Storage to persist Trust ID records.
 * @param keyStoreManagerFactory A factory for creating KeyStoreManaging instances for a given alias.
 */
class TrustID(
    config: LoginIDConfig,
    private val storage: Storage,
    private val keyStoreManagerFactory: (String) -> KeyStoreManaging
) : TrustIDManaging {
    private val appId = config.getAppId()
    private val service = "com.loginid.trustid"
    private val anonymousUsername = ""
    private val moshi = Moshi.Builder().build()
    private val recordAdapter = moshi.adapter(TrustIDRecord::class.java)
    private val jwtHeaderAdapter = moshi.adapter(JWTHeader::class.java)
    private val jwtPayloadAdapter = moshi.adapter(JWTPayload::class.java)

    private val accountsKey = StorageKey<Set<String>>("$service:accounts")
    private fun recordKey(account: String) = StorageKey<String>("$service:$account")

    /**
     * Builds the account identifier used to store a Trust ID record in the storage.
     *
     * The account key combines the application ID and username to ensure
     * Trust IDs are uniquely namespaced per application and user.
     *
     * @param username The username associated with the Trust ID.
     * @return A unique account identifier suitable for storage.
     */
    private fun accountKey(username: String): String {
        return "$appId::$username"
    }

    /**
     * Deletes all of the stored wallet identity from the storage.
     */
    override suspend fun deleteTrustId() {
        val accounts = storage.get(accountsKey) ?: return
        for (account in accounts) {
            // Also delete the key from keystore
            val recordJson = storage.get(recordKey(account))
            if (recordJson != null) {
                try {
                    val record = recordAdapter.fromJson(recordJson)
                    if (record != null) {
                        keyStoreManagerFactory(record.keyAlias).deleteKeyPair()
                    }
                } catch (_: Exception) {
                    // Ignore malformed records
                }
            }
            storage.delete(recordKey(account))
        }
        storage.delete(accountsKey)
    }

    /**
     * Signs a new or existing Trust ID using its associated private key.
     *
     * If a key pair is already stored, it is reused. Otherwise, a new key pair is generated and stored.
     *
     * @return A signed JWT representing the trust ID.
     * @throws LoginIDError for known LoginID API errors.
     */
    override suspend fun signWithTrustId(): String {
        // Find most recently used TrustID for this app.
        val recentlyUsed = getMostRecentlyUsedTrustId()
        if (recentlyUsed != null) {
            return sign(recentlyUsed.first)
        }

        // No TrustIDs exist for this app yet.
        return signWithTrustId(null)
    }

    /**
     * Signs a Trust ID for the specified username.
     *
     * If a Trust ID record already exists for the provided username,
     * the existing record is reused and its last-used timestamp is updated.
     * Otherwise, a new Trust ID record and key pair are created and stored.
     *
     * @param username The username associated with the Trust ID.
     *   If `null`, an anonymous Trust ID is used.
     * @return A signed JWT representing the Trust ID.
     * @throws LoginIDError for known LoginID API errors.
     */
    override suspend fun signWithTrustId(username: String?): String {
        val normalizedUsername = username ?: anonymousUsername
        val account = accountKey(normalizedUsername)
        val storageKey = recordKey(account)
        val recordJson = storage.get(storageKey)

        if (recordJson != null) {
            val record = recordAdapter.fromJson(recordJson)
                ?: throw LoginIDError("json_error", "Failed to parse TrustIDRecord")
            record.lastUsedAt = System.currentTimeMillis()
            val updatedData = recordAdapter.toJson(record)
            storage.put(storageKey, updatedData)
            return sign(record)
        }

        return createAndSign(normalizedUsername)
    }

    /**
     * Creates and stores a new Trust ID record for the specified username,
     * then signs and returns a Trust ID JWT.
     *
     * A new ES256 key pair is generated and persisted alongside metadata
     * describing the Trust ID record.
     *
     * @param username The username associated with the new Trust ID.
     * @return A signed JWT representing the newly created Trust ID.
     * @throws LoginIDError If key generation, encoding, persistence, or signing fails.
     */
    private suspend fun createAndSign(username: String): String {
        val recordId = UUID.randomUUID().toString()
        val keyAlias = "$service:$recordId"
        val keyManager = keyStoreManagerFactory(keyAlias)
        keyManager.generateES256KeyPair()

        val record = TrustIDRecord(
            id = recordId,
            appId = appId,
            username = username,
            keyAlias = keyAlias,
            lastUsedAt = System.currentTimeMillis()
        )

        val data = recordAdapter.toJson(record)
        val account = accountKey(username)
        storage.put(recordKey(account), data)

        storage.update(accountsKey) { currentAccounts ->
            (currentAccounts ?: emptySet()) + account
        }

        return sign(record)
    }

    /**
     * Signs a Trust ID record using its associated private key.
     *
     * The key pair is reconstructed from the stored record and used to
     * produce a signed JWT payload.
     *
     * @param record The Trust ID record containing the stored key material.
     * @return A signed JWT representing the Trust ID.
     * @throws LoginIDError If key reconstruction or signing fails.
     */
    private fun sign(record: TrustIDRecord): String {
        val keyManager = keyStoreManagerFactory(record.keyAlias)
        val payload = toTrustIdPayload()
        return signJwtWithJwk(payload, keyManager)
    }

    /**
     * Signs a JWT with a given payload and key manager using the ES256 algorithm.
     *
     * @param payload The payload to encode and sign.
     * @param keyManager The key manager to use for signing.
     * @return A fully signed JWT string.
     * @throws LoginIDError If signing fails.
     */
    private fun signJwtWithJwk(payload: JWTPayload, keyManager: KeyStoreManaging): String {
        val jwk = keyManager.exportPublicKeyJwk()
            ?: throw LoginIDError("key_error", "Failed to export public key JWK")

        val header = JWTHeader(alg = "ES256", jwk = jwk)

        val encodedHeader = jwtHeaderAdapter.toJson(header).toByteArray(Charsets.UTF_8).base64URLEncode()
        val encodedPayload = jwtPayloadAdapter.toJson(payload).toByteArray(Charsets.UTF_8).base64URLEncode()
        val unsignedToken = "$encodedHeader.$encodedPayload"
        val signature = keyManager.signWithES256PrivateKey(unsignedToken)
            ?: throw LoginIDError("signing_error", "Failed to sign with private key")

        return "$unsignedToken.$signature"
    }

    /**
     * Retrieves the most recently used Trust ID record for the current application.
     *
     * Only records associated with the current application ID are considered.
     * The record with the most recent `lastUsedAt` value is returned.
     *
     * @return A tuple containing the Trust ID record and its Keychain account
     *   identifier, or `nil` if no matching records exist.
     */
    private suspend fun getMostRecentlyUsedTrustId(): Pair<TrustIDRecord, String>? {
        val accounts = storage.get(accountsKey) ?: return null

        val records = accounts.mapNotNull { account ->
            val recordJson = storage.get(recordKey(account))
            if (recordJson != null) {
                try {
                    val record = recordAdapter.fromJson(recordJson)
                    if (record != null && record.appId == appId) {
                        return@mapNotNull record to account
                    }
                } catch (_: Exception) {
                    // Ignore malformed records
                }
            }
            null
        }

        return records.maxByOrNull { it.first.lastUsedAt }
    }

    /**
     * Constructs a payload object for the trust ID JWT.
     *
     * @return A `JWTPayload` containing the ID.
     */
    private fun toTrustIdPayload(): JWTPayload {
        return JWTPayload()
    }
}
