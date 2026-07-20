package io.loginid.core.stores

import io.loginid.core.interfaces.Storage
import io.loginid.core.interfaces.StorageKey
import io.loginid.core.models.LoginIDConfig

/**
 * Manages the storage of the device ID.
 *
 * This class provides a simple interface to get and set the device ID,
 * abstracting the underlying storage mechanism.
 *
 * @property storage The storage implementation to use for persisting the device ID.
 * @param config The LoginID configuration, used to create a unique storage key.
 */
class DeviceStore(
    private val storage: Storage,
    config: LoginIDConfig
) {
    private val deviceIdKey = StorageKey<String>("com.loginid.deviceid.${config.getAppId()}")

    /**
     * Retrieves the stored device ID.
     *
     * @return The device ID as a String, or null if it's not found.
     */
    suspend fun getDeviceId(): String? {
        return storage.get(deviceIdKey)
    }

    /**
     * Stores the given device ID.
     *
     * If the provided deviceId is null, this function does nothing.
     * @param deviceId The device ID to store.
     */
    suspend fun setDeviceId(deviceId: String?) {
        deviceId?.let {
            storage.put(deviceIdKey, it)
        }
    }
}
