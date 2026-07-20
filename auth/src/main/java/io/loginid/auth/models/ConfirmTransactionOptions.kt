package io.loginid.auth.models

/**
 * Options for configuring a transaction confirmation request.
 *
 * @property nonce A unique nonce to ensure the transaction's integrity and prevent replay attacks. If not provided, a new UUID will be generated.
 * @property txType The type of transaction payload, such as `raw` or a custom format. Defaults to `raw` if not specified.
 */
data class ConfirmTransactionOptions(
    val nonce: String? = null,
    val txType: String? = null
)
