package com.loginid.core.errors

import kotlin.reflect.KClass

/**
 * Represents a detailed error from the LoginID API.
 *
 * @property msgCode A short code representing the error type.
 * @property msg A detailed, human-readable error message.
 * @property message The detailed error message, often the same as msg.
 */
class LoginIDError(
    var msgCode: String? = "unknown_error",
    var msg: String? = null,
    override var message: String? = null
) : Exception() {

    init {
        if (message.isNullOrBlank() && msg.isNullOrBlank()) {
            message = "Unknown error"
            msg = "Unknown error"
        } else if (!msg.isNullOrBlank()) {
            message = msg
        } else if (!message.isNullOrBlank()) {
            msg = message
        }
    }

    companion object {
        /**
         * Creates a generic unknown error.
         *
         * @return A LoginIDError instance for an unknown error.
         */
        fun unknownError(): LoginIDError {
            return LoginIDError(
                "unknown_error",
                "Unknown error",
                "Unknown error"
            )
        }

        /**
         * Creates an error for unauthorized access.
         *
         * @return A LoginIDError instance for an unauthorized error.
         */
        fun unauthorizedError(): LoginIDError {
            val error = LoginIDError()
            error.msgCode = "unauthorized"
            error.message = "User needs to login to perform this action"
            error.msg = "User needs to login to perform this action"
            return error
        }

        /**
         * Creates an error for when no login options are available.
         *
         * @return A LoginIDError instance indicating no login options.
         */
        fun noLoginOptionsError(): LoginIDError {
            val error = LoginIDError()
            error.msgCode = "no_login_options"
            error.message = "No login options available"
            error.msg = "No login options available"
            return error
        }

        /**
         * Creates an error for an invalid enum value.
         *
         * @param T The type of the enum.
         * @param invalidValue The invalid value that was provided.
         * @param enumClass The class of the enum to list valid options.
         * @return A LoginIDError instance for an invalid enum value.
         */
        fun <T : Enum<T>> invalidEnumValueError(
            invalidValue: String?,
            enumClass: KClass<T>
        ): LoginIDError {
            val error = LoginIDError()
            val validOptions = enumClass.java.enumConstants
                .joinToString(", ") { it.name.lowercase() }
            error.msgCode = "invalid_value"
            error.message = "Invalid value: $invalidValue. Valid options are: $validOptions"
            error.msg = "Invalid value: $invalidValue. Valid options are: $validOptions"
            return error
        }
    }

    /**
     * Provides a string representation of the LoginIDError.
     *
     * @return A formatted string containing the error code and message.
     */
    override fun toString(): String {
        val message = msg ?: super.message
        return "LoginIDError(\n" +
                "$msgCode\n" +
                "$message\n" +
                ")"
    }
}
