package com.loginid.core.interfaces

/**
 * An interface for a simple key-value storage system.
 *
 * Implementations of this interface provide mechanisms to store, retrieve,
 * update, and delete data asynchronously.
 */
interface Storage {
    /**
     * Retrieves a value for a given key.
     *
     * @param key The [StorageKey] to look up.
     * @return The value associated with the key, or null if not found.
     */
    suspend fun <T> get(
        key: StorageKey<T>
    ): T?

    /**
     * Stores a value for a given key.
     *
     * If a value already exists for the key, it will be overwritten.
     *
     * @param key The [StorageKey] to associate the value with.
     * @param value The value to store.
     */
    suspend fun <T> put(
        key: StorageKey<T>,
        value: T
    )

    /**
     * Atomically updates a value for a given key using a transformation function.
     *
     * @param key The [StorageKey] of the value to update.
     * @param transform A function that takes the current value (or null) and returns the new value.
     * If the transform returns null, the key should be removed.
     * @return The new value after the transformation.
     */
    suspend fun <T> update(
        key: StorageKey<T>,
        transform: (T?) -> T?
    ): T?

    /**
     * Deletes a value for a given key.
     *
     * @param key The [StorageKey] to delete.
     * @return `true` if the key was found and deleted, `false` otherwise.
     */
    suspend fun delete(
        key: StorageKey<*>
    ): Boolean

    /**
     * Deletes all key-value pairs from the storage.
     */
    suspend fun deleteAll()
}

/**
 * A type-safe key for use with the [Storage] interface.
 *
 * @param T The type of the value associated with this key.
 * @property key The unique string identifier for the storage entry.
 * @property value A default value, not used by the storage implementation itself.
 */
class StorageKey<T>(
    val key: String,
    val value: T? = null
)
