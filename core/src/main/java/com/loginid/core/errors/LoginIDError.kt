package com.loginid.core.errors

import kotlin.reflect.KClass

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
        fun unknownError(): LoginIDError {
            return LoginIDError(
                "unknown_error",
                "Unknown error",
                "Unknown error"
            )
        }

        fun unauthorizedError(): LoginIDError {
            val error = LoginIDError()
            error.msgCode = "unauthorized"
            error.message = "User needs to login to perform this action"
            error.msg = "User needs to login to perform this action"
            return error
        }

        fun noLoginOptionsError(): LoginIDError {
            val error = LoginIDError()
            error.msgCode = "no_login_options"
            error.message = "No login options available"
            error.msg = "No login options available"
            return error
        }

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

    override fun toString(): String {
        val message = msg ?: super.message
        return "LoginIDError(\n" +
                "$msgCode\n" +
                "$message\n" +
                ")"
    }
}