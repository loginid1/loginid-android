package com.loginid.auth.models

import io.loginid.client.model.TxComplete

/**
 * The result of a successful transaction confirmation.
 *
 * @property token An authorization token that confirms the transaction.
 * @property credentialId The identifier of the passkey credential used for confirmation.
 * @property passkey Optional details about the passkey used for confirmation.
 */
data class TxConfirmResult(
    val token: String,
    val credentialId: String,
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
