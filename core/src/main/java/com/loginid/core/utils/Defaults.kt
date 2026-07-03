package com.loginid.core.utils

import android.app.Activity
import com.loginid.client.model.DeviceInfo
import com.loginid.client.model.User
import com.loginid.client.model.UserLogin
import com.loginid.core.enums.UsernameType
import com.loginid.core.models.LoginIDConfig
import com.loginid.core.stores.DeviceStore
import java.util.UUID

/**
 * A utility object for providing default values for various parameters used in the SDK.
 */
object Defaults {

    /**
     * Returns the given display name if it's not null or empty, otherwise returns the username.
     *
     * @param value The display name to check.
     * @param username The username to use as a fallback.
     * @return The display name or username.
     */
    fun displayName(value: String?, username: String): String {
        return if (!value.isNullOrEmpty()) value else username
    }

    /**
     * Converts the core UsernameType to the client model User.UsernameType.
     *
     * @param value The username type.
     * @return The corresponding client model username type.
     */
    private fun toUserUsernameType(value: UsernameType?): User.UsernameType {
        return User.UsernameType.valueOf(value?.name ?: "OTHER")
    }

    /**
     * Converts the core UsernameType to the client model UserLogin.UsernameType.
     *
     * @param value The username type.
     * @return The corresponding client model username type.
     */
    private fun toUserLoginUsernameType(value: UsernameType?): UserLogin.UsernameType {
        return UserLogin.UsernameType.valueOf(value?.name ?: "OTHER")
    }

    /**
     * Returns the given nonce if it's not null or empty, otherwise generates a new UUID.
     *
     * @param value The nonce to check.
     * @return The nonce or a new UUID.
     */
    fun nonce(value: String?): String {
        return if (!value.isNullOrEmpty()) value else UUID.randomUUID().toString()
    }

    /**
     * Returns the given transaction type if it's not null or empty, otherwise returns "raw".
     *
     * @param value The transaction type.
     * @return The transaction type or "raw".
     */
    fun txType(value: String?): String {
        return if (!value.isNullOrEmpty()) value else "raw"
    }

    /**
     * Returns the given boolean value, or false if it's null.
     *
     * @param value The boolean value.
     * @return The boolean value or false.
     */
    fun autoFill(value: Boolean?): Boolean {
        return value ?: false
    }

    /**
     * Constructs a `User` object from the given parameters, using default values where necessary.
     *
     * @param displayName The user's display name.
     * @param username The user's username.
     * @param usernameType The type of username.
     * @return A `User` object.
     */
    fun user(
        displayName: String?,
        username: String?,
        usernameType: UsernameType?
    ): User {
        return User(
            displayName = displayName(displayName, username ?: ""),
            username = username ?: "",
            usernameType = toUserUsernameType(usernameType)
        )
    }

    /**
     * Constructs a `UserLogin` object from the given parameters, using default values where necessary.
     *
     * @param username The user's username.
     * @param usernameType The type of username.
     * @return A `UserLogin` object.
     */
    fun userLogin(
        username: String?,
        usernameType: UsernameType?
    ): UserLogin {
        return UserLogin(
            username = username ?: "",
            usernameType = toUserLoginUsernameType(usernameType)
        )
    }

    /**
     * Retrieves device information, using a stored device ID if a new one is not provided.
     *
     * This function fetches device metadata using [DeviceUtils.getDeviceInfo]. If a `deviceId`
     * is not passed, it attempts to retrieve one from the provided [DeviceStore].
     *
     * @param activity The current activity, required to access system services.
     * @param store The [DeviceStore] used to retrieve a stored device ID.
     * @param deviceId An optional device ID to use. If null, the stored ID is used.
     * @return A [DeviceInfo] object containing the device's metadata.
     */
    suspend fun deviceInfo(
        activity: Activity,
        store: DeviceStore,
        deviceId: String? = null
    ): DeviceInfo {
        return DeviceUtils.getDeviceInfo(
            activity,
            deviceId ?: store.getDeviceId()
        )
    }

    /**
     * Generates a map of trust-related identifiers.
     *
     * This function generates a wallet ID, merchant ID, and authentication ID based on the provided parameters.
     * The resulting map contains only the non-null identifiers.
     *
     * @param config The LoginID configuration.
     * @param store The Trust ID manager.
     * @param txPayload The transaction payload, if any. If provided, a wallet ID will be generated.
     * @param merchantTrustId The merchant's Trust ID, if any.
     * @param username The username. Used to generate an auth ID under specific conditions.
     * @return A map of trust-related identifiers ("auth", "wallet", "merchant").
     */
    suspend fun trustItems(
        config: LoginIDConfig,
        store: TrustID,
        txPayload: String? = null,
        merchantTrustId: String? = null,
        username: String
    ): Map<String, String> {
        val walletId: String? = if (txPayload != null) {
            store.signWithTrustId()
        } else {
            null
        }
        val merchantId: String? = if (merchantTrustId?.isNotEmpty() == true) merchantTrustId else null
        val authId: String? =
            if (config.useTrustId() && walletId == null && merchantId == null && username.isNotEmpty()) {
                store.signWithTrustId(username)
            } else {
                null
            }

        val items = mutableMapOf<String, String>()
        authId?.let { items["auth"] = it }
        walletId?.let { items["wallet"] = it }
        merchantId?.let { items["merchant"] = it }

        return items
    }
}
