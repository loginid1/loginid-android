package com.loginid.core.interfaces

import io.loginid.client.model.AuthCompleteRequestBody
import io.loginid.client.model.AuthInit
import io.loginid.client.model.AuthInitRequestBody
import io.loginid.client.model.JWT
import io.loginid.client.model.RegCompleteRequestBody
import io.loginid.client.model.RegInit
import io.loginid.client.model.RegInitRequestBody
import io.loginid.client.model.TxComplete
import io.loginid.client.model.TxCompleteRequestBody
import io.loginid.client.model.TxInit
import io.loginid.client.model.TxInitRequestBody

/**
 * An interface for the Passkey API, defining methods for registration, authentication,
 * and transaction confirmation with passkeys.
 */
interface PasskeyAPI {
    /**
     * Initiates the passkey registration process.
     *
     * @param request The request body for registration initialization.
     * @param userAgent The user agent string of the client.
     * @param authorization The authorization token.
     * @return A [RegInit] object containing registration details.
     */
    suspend fun regInit(request: RegInitRequestBody, userAgent: String?, authorization: String?): RegInit

    /**
     * Completes the passkey registration process.
     *
     * @param request The request body for registration completion.
     * @return A [JWT] object upon successful registration.
     */
    suspend fun regComplete(request: RegCompleteRequestBody): JWT

    /**
     * Initiates the passkey authentication process.
     *
     * @param request The request body for authentication initialization.
     * @param userAgent The user agent string of the client.
     * @return An [AuthInit] object containing authentication challenge details.
     */
    suspend fun authInit(request: AuthInitRequestBody, userAgent: String?): AuthInit

    /**
     * Completes the passkey authentication process.
     *
     * @param request The request body for authentication completion.
     * @return A [JWT] object upon successful authentication.
     */
    suspend fun authComplete(request: AuthCompleteRequestBody): JWT

    /**
     * Initiates a transaction confirmation process with a passkey.
     *
     * @param request The request body for transaction initialization.
     * @return A [TxInit] object containing transaction details.
     */
    suspend fun txInit(request: TxInitRequestBody): TxInit

    /**
     * Completes a transaction confirmation process with a passkey.
     *
     * @param request The request body for transaction completion.
     * @return A [TxComplete] object confirming the transaction.
     */
    suspend fun txComplete(request: TxCompleteRequestBody): TxComplete
}
