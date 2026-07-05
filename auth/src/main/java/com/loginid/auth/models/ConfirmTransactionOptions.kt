package com.loginid.auth.models

/**
 * Options for configuring a transaction confirmation request.
 */
data class ConfirmTransactionOptions(
    /**
     * A unique nonce to ensure the transaction's integrity and prevent replay attacks
     * If not provided, a new UUID will be generated.
     */
    val nonce: String? = null,

    /**
     * The type of transaction payload, such as `raw` or a custom format.
     * Defaults to `raw` if not specified.
     */
    val txType: String? = null
)
