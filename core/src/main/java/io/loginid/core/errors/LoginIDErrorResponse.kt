package io.loginid.core.errors

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents the error response structure from the LoginID API.
 * This class is used for deserializing JSON error responses.
 */
@JsonClass(generateAdapter = true)
data class LoginIDErrorResponse(
    /**
     * A short code representing the error type.
     */
    @property:Json(name = "msgCode")
    val msgCode: String? = "unknown_error",

    /**
     * A detailed, human-readable error message.
     */
    @property:Json(name = "msg")
    val msg: String? = null,

    /**
     * The detailed error message, often the same as msg.
     */
    @property:Json(name = "message")
    val message: String? = null
) {
    /**
     * Converts this data transfer object to a LoginIDError exception.
     *
     * @return A LoginIDError instance with the same error details.
     */
    fun toLoginIDError(): LoginIDError {
        return LoginIDError(
            msgCode = this.msgCode,
            msg = this.msg,
            message = this.message
        )
    }
}
