package io.loginid.core.stores

import android.content.Context
import android.content.SharedPreferences
import io.loginid.core.interfaces.Storage
import io.loginid.core.interfaces.StorageKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A SharedPreferences-based implementation of the [Storage] interface.
 *
 * This class provides a way to store and retrieve key-value pairs using Android's SharedPreferences.
 * It supports basic data types like String, Int, Long, Float, Boolean, and Set<String>.
 * All operations are performed asynchronously on a background thread.
 *
 * @property context The application context, used to access SharedPreferences.
 * @property preferenceName The name of the SharedPreferences file.
 */
class SharedPreferencesStorage(
    private val context: Context,
    private val preferenceName: String = "io.loginid.storage"
) : Storage {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    }

    /**
     * Retrieves a value from SharedPreferences for the given key.
     *
     * @param key The [StorageKey] identifying the value to retrieve.
     * @return The value associated with the key, or null if not found.
     * @throws ClassCastException if the stored value cannot be cast to the expected type.
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> get(key: StorageKey<T>): T? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.all[key.key] as? T
        }
    }

    /**
     * Stores a value in SharedPreferences.
     *
     * @param key The [StorageKey] to associate the value with.
     * @param value The value to store.
     * @throws IllegalArgumentException if the value type is not supported by SharedPreferences.
     */
    override suspend fun <T> put(key: StorageKey<T>, value: T) {
        withContext(Dispatchers.IO) {
            val editor = sharedPreferences.edit()
            when (value) {
                is String -> editor.putString(key.key, value)
                is Int -> editor.putInt(key.key, value)
                is Long -> editor.putLong(key.key, value)
                is Float -> editor.putFloat(key.key, value)
                is Boolean -> editor.putBoolean(key.key, value)
                is Set<*> -> {
                    // SharedPreferences only supports Set<String>
                    if (value.all { it is String }) {
                        @Suppress("UNCHECKED_CAST")
                        editor.putStringSet(key.key, value as Set<String>)
                    } else {
                        throw IllegalArgumentException("Only Set<String> is supported by SharedPreferences.")
                    }
                }

                else -> throw IllegalArgumentException("Unsupported type for SharedPreferences: ${value?.let { it::class.java.name }}")
            }
            editor.apply()
        }
    }

    /**
     * Atomically updates a value in SharedPreferences.
     *
     * This operation retrieves the current value, applies the transformation function,
     * and then stores the new value.
     *
     * @param key The [StorageKey] for the value to update.
     * @param transform A function that takes the current value (or null) and returns the new value.
     * @return The newly stored value.
     */
    override suspend fun <T> update(key: StorageKey<T>, transform: (T?) -> T?): T? {
        return withContext(Dispatchers.IO) {
            val currentValue = get(key)
            val newValue = transform(currentValue)
            if (newValue != null) {
                put(key, newValue)
            } else {
                delete(key)
            }
            newValue
        }
    }

    /**
     * Deletes a value from SharedPreferences.
     *
     * @param key The [StorageKey] of the value to delete.
     * @return `true` if the value was successfully deleted, `false` otherwise.
     */
    override suspend fun delete(key: StorageKey<*>): Boolean {
        return withContext(Dispatchers.IO) {
            if (sharedPreferences.contains(key.key)) {
                sharedPreferences.edit().remove(key.key).apply()
                true
            } else {
                false
            }
        }
    }

    /**
     * Deletes all key-value pairs from SharedPreferences.
     */
    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().clear().apply()
        }
    }
}
