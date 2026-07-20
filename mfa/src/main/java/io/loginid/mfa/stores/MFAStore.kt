package io.loginid.mfa.stores

import io.loginid.core.interfaces.Storage
import io.loginid.core.interfaces.StorageKey
import io.loginid.core.models.LoginIDConfig
import io.loginid.mfa.models.MFAInfo
import io.loginid.mfa.models.TrustSet
import com.squareup.moshi.Moshi

/**
 * Handles secure storage and retrieval of Multi-Factor Authentication (MFA) session data,
 * scoped by a given App ID.
 *
 * @property storage The storage implementation to use for persisting MFA data.
 * @param config The LoginID configuration, used to create unique storage keys.
 */
internal class MFAStore(
    private val storage: Storage,
    config: LoginIDConfig
) {
    private val mfaInfoKey = StorageKey<String>("io.loginid.mfa.info.${config.getAppId()}")
    private val trustSetKey = StorageKey<String>("io.loginid.mfa.trustset.${config.getAppId()}")
    private val moshi = Moshi.Builder().build()
    private val mfaInfoAdapter = moshi.adapter(MFAInfo::class.java)
    private val trustSetAdapter = moshi.adapter(TrustSet::class.java)

    /**
     * Persists the current MFA session information.
     *
     * @param info The [MFAInfo] to store. If `null`, the call is ignored.
     */
    internal suspend fun storeMFAInfo(info: MFAInfo?) {
        info?.let {
            val mfaInfoJson = mfaInfoAdapter.toJson(it)
            storage.put(mfaInfoKey, mfaInfoJson)
        }
    }

    /**
     * Retrieves the stored MFA session information, if available.
     *
     * @return A decoded [MFAInfo] instance, or `null` if not found or if decoding fails.
     */
    internal suspend fun getMFAInfo(): MFAInfo? {
        val mfaInfoJson = storage.get(mfaInfoKey) ?: return null
        return try {
            mfaInfoAdapter.fromJson(mfaInfoJson)
        } catch (_: Exception) {
            // Data is corrupt or incompatible; clear and return nil.
            storage.delete(mfaInfoKey)
            null
        }
    }

    /**
     * Removes any persisted MFA session information.
     *
     * Use this when the MFA flow is completed or intentionally reset.
     */
    internal suspend fun clearMFAInfo() {
        storage.delete(mfaInfoKey)
    }

    /**
     * Persists the current Trust set information.
     *
     * @param trustSet The [TrustSet] to store. If `null`, the call is ignored.
     */
    internal suspend fun storeTrustSet(trustSet: TrustSet?) {
        trustSet?.let {
            val trustSetJson = trustSetAdapter.toJson(it)
            storage.put(trustSetKey, trustSetJson)
        }
    }

    /**
     * Retrieves the stored trust set information, if available.
     *
     * @return A decoded [TrustSet] instance, or `null` if not found or if decoding fails.
     */
    internal suspend fun getTrustSet(): TrustSet? {
        val trustSetJson = storage.get(trustSetKey) ?: return null
        return try {
            trustSetAdapter.fromJson(trustSetJson)
        } catch (_: Exception) {
            // Data is corrupt or incompatible; clear and return nil.
            storage.delete(trustSetKey)
            null
        }
    }

    /**
     * Removes any persisted trust set information.
     *
     * Use this when the MFA flow is completed or intentionally reset.
     */
    internal suspend fun clearTrustSet() {
        storage.delete(trustSetKey)
    }

    /**
     * Updates the session property of the stored MFA information.
     *
     * @param session The new session string to store. If `null`, the session is not updated.
     */
    internal suspend fun updateMFASession(session: String?) {
        session?.let { newSession ->
            getMFAInfo()?.let { oldInfo ->
                val newInfo = MFAInfo(oldInfo, newSession)
                storeMFAInfo(newInfo)
            }
        }
    }
}
