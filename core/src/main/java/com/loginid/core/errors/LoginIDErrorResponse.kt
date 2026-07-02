package com.loginid.core.errors

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginIDErrorResponse(
    @property:Json(name = "msgCode")
    val msgCode: String? = "unknown_error",

    @property:Json(name = "msg")
    val msg: String? = null,

    @property:Json(name = "message")
    val message: String? = null
) {
    fun toLoginIDError(): LoginIDError {
        return LoginIDError(
            msgCode = this.msgCode,
            msg = this.msg,
            message = this.message
        )
    }
}