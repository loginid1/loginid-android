package com.loginid.core.errors

import com.squareup.moshi.Moshi
import org.openapitools.client.infrastructure.ClientError
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerError
import org.openapitools.client.infrastructure.ServerException

/**
 * A custom exception class for handling errors specific to the LoginID SDK.
 *
 * @param message A descriptive error message.
 */
class LoginIDException(message: String) : RuntimeException(message) {
    companion object {
        /**
         * A lazy-initialized instance of Moshi for JSON parsing.
         */
        private val moshi by lazy { Moshi.Builder().build() }
        /**
         * A lazy-initialized JSON adapter for LoginIDErrorResponse.
         */
        private val jsonAdapter by lazy { moshi.adapter(LoginIDErrorResponse::class.java) }

        /**
         * Parses a generic Exception and converts it into a LoginIDError.
         *
         * This function is capable of handling ClientException and ServerException from the OpenAPI client,
         * parsing their response bodies to extract detailed error information.
         *
         * @param e The exception to parse.
         * @return A LoginIDError representing the parsed exception.
         * @throws Exception if the exception type is not recognized and cannot be parsed.
         */
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
