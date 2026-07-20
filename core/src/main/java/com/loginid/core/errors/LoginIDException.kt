package com.loginid.core.errors

import com.squareup.moshi.Moshi
import io.loginid.client.infrastructure.ClientError
import io.loginid.client.infrastructure.ClientException
import io.loginid.client.infrastructure.ServerError
import io.loginid.client.infrastructure.ServerException
import kotlin.reflect.KClass

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
         * Parses the body of a ClientException into a specified data class.
         *
         * @param e The ClientException to parse.
         * @param clazz The class to which the JSON body should be converted.
         * @return An instance of the specified class, or null if parsing fails.
         */
        fun <T : Any> parseClientErrorBody(e: ClientException, clazz: KClass<T>): T? {
            return try {
                val errorBody = (e.response as? ClientError<*>)?.body?.toString() ?: return null
                moshi.adapter(clazz.java).fromJson(errorBody)
            } catch (_: Exception) {
                null
            }
        }

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
                    val errorBody = (e.response as? ClientError<*>)?.body?.toString()
                    val parsedError = errorBody?.let { jsonAdapter.fromJson(it) }
                    parsedError?.toLoginIDError() ?: LoginIDError.unknownError()
                }
                is ServerException -> {
                    val errorBody = (e.response as? ServerError<*>)?.body?.toString()
                    val parsedError = errorBody?.let { jsonAdapter.fromJson(it) }
                    parsedError?.toLoginIDError() ?: LoginIDError.unknownError()
                }
                else -> throw e
            }
        }
    }
}
