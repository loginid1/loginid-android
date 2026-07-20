package io.loginid.mfa.models

import com.squareup.moshi.JsonClass

/**
 * A set of checkout trust tokens.
 *
 * @property merchantTrustId The merchant trust ID.
 * @property walletTrustId The wallet trust ID.
 */
@JsonClass(generateAdapter = true)
internal data class TrustSet(
    val merchantTrustId: String?,
    val walletTrustId: String?
)
