package com.loginid.auth.models

import com.loginid.client.model.TxComplete

/**
 * The result of a successful transaction confirmation.
 */
data class TxConfirmResult(
    /**
     * An authorization token that confirms the transaction.
     */
    val token: String,
    /**
     * The identifier of the passkey credential used for confirmation.
     */
    val credentialId: String,
    /**
     * Optional details about the passkey used for confirmation.
     */
    val passkey: PasskeyDetails?
) {
    /**
     * Initializes a [TxConfirmResult] from a [TxComplete] API response.
     * @param result The [TxComplete] object from the API.
     */
    internal constructor(result: TxComplete) : this(
        token = result.token,
        credentialId = result.credentialId,
        passkey = result.authCred?.let { PasskeyDetails(it) }
    )
}
