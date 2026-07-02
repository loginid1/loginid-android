package com.loginid.core.errors

import com.squareup.moshi.Moshi
import org.openapitools.client.infrastructure.ClientError
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerError
import org.openapitools.client.infrastructure.ServerException

class LoginIDException(message: String) : RuntimeException(message) {
    companion object {
        private val moshi by lazy { Moshi.Builder().build() }
        private val jsonAdapter by lazy { moshi.adapter(LoginIDErrorResponse::class.java) }

        fun parseError(e: Exception): LoginIDError {
            return when (e) {
                is ClientException -> {
                    val errorBody = (e.response as ClientError<*>).body.toString()
                    val parsedError = jsonAdapter.fromJson(errorBody)
                    parsedError?.toLoginIDError() ?: LoginIDError.unknownError()
                }
                is ServerException -> {
                    val errorBody = (e.response as ServerError<*>).body.toString()
                    val parsedError = jsonAdapter.fromJson(errorBody)
                    parsedError?.toLoginIDError() ?: LoginIDError.unknownError()
                }
                else -> throw e
            }
        }
    }
}