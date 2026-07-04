package com.loginid.core.services

import com.loginid.client.api.AuthApi
import com.loginid.client.api.RegApi
import com.loginid.client.api.TxApi
import com.loginid.client.model.AuthCompleteRequestBody
import com.loginid.client.model.AuthInit
import com.loginid.client.model.AuthInitRequestBody
import com.loginid.client.model.JWT
import com.loginid.client.model.RegCompleteRequestBody
import com.loginid.client.model.RegInit
import com.loginid.client.model.RegInitRequestBody
import com.loginid.client.model.TxComplete
import com.loginid.client.model.TxCompleteRequestBody
import com.loginid.client.model.TxInit
import com.loginid.client.model.TxInitRequestBody
import com.loginid.core.interfaces.PasskeyAPI
import com.loginid.core.models.LoginIDConfig

/**
 * A service class that implements the [PasskeyAPI] interface to interact with the LoginID Passkey API.
 */
class PasskeyService(config: LoginIDConfig) : PasskeyAPI {
    private val baseUrl = config.getBaseUrl()
    private val regApi = RegApi(baseUrl)
    private val authApi = AuthApi(baseUrl)
    private val txApi = TxApi(baseUrl)

    /**
     * Initiates the passkey registration process.
     *
     * @param request The request body for registration initialization.
     * @param userAgent The user agent string of the client.
     * @param authorization The authorization token.
     * @return A [RegInit] object containing registration details.
     */
    override suspend fun regInit(request: RegInitRequestBody, userAgent: String?, authorization: String?): RegInit {
        return regApi.regRegInit(
            regInitRequestBody = request,
            userAgent = userAgent,
            authorization = authorization
        )
    }

    /**
     * Completes the passkey registration process.
     *
     * @param request The request body for registration completion.
     * @return A [JWT] object upon successful registration.
     */
    override suspend fun regComplete(request: RegCompleteRequestBody): JWT {
        return regApi.regRegComplete(regCompleteRequestBody = request)
    }

    /**
     * Initiates the passkey authentication process.
     *
     * @param request The request body for authentication initialization.
     * @param userAgent The user agent string of the client.
     * @return An [AuthInit] object containing authentication challenge details.
     */
    override suspend fun authInit(request: AuthInitRequestBody, userAgent: String?): AuthInit {
        return authApi.authAuthInit(
            authInitRequestBody = request,
            userAgent = userAgent
        )
    }

    /**
     * Completes the passkey authentication process.
     *
     * @param request The request body for authentication completion.
     * @return A [JWT] object upon successful authentication.
     */
    override suspend fun authComplete(request: AuthCompleteRequestBody): JWT {
        return authApi.authAuthComplete(authCompleteRequestBody = request)
    }

    /**
     * Initiates a transaction confirmation process with a passkey.
     *
     * @param request The request body for transaction initialization.
     * @return A [TxInit] object containing transaction details.
     */
    override suspend fun txInit(request: TxInitRequestBody): TxInit {
        return txApi.txTxInit(txInitRequestBody = request)
    }

    /**
     * Completes a transaction confirmation process with a passkey.
     *
     * @param request The request body for transaction completion.
     * @return A [TxComplete] object confirming the transaction.
     */
    override suspend fun txComplete(request: TxCompleteRequestBody): TxComplete {
        return txApi.txTxComplete(txCompleteRequestBody = request)
    }
}
