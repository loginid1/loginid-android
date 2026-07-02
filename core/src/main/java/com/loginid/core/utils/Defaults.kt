package com.loginid.core.utils

import com.loginid.client.model.User
import com.loginid.client.model.UserLogin
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
     * Returns the raw value of the username type, or ".other" if it's null.
     *
     * @param value The username type.
     * @return The usernametype.
     */
    fun usernameType(value: User.UsernameType?): User.UsernameType {
        return value?.value ?: User.UsernameType.OTHER
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
            usernameType = usernameType(usernameType)
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
            usernameType = usernameType(usernameType)
        )
    }
}
