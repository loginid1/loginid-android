package com.loginid.mfa.utils

import com.loginid.client.model.MfaActionAction
import com.loginid.client.model.MfaNext
import com.loginid.mfa.models.TrustSet

/**
 * A utility for locating trust tokens within a Mfa Begin response.
 */
internal object TrustTokenFinder {
    private const val MERCHANT_TRUST_ID = "merchantTrustID"
    private const val WALLET_TRUST_ID = "walletTrustID"

    /**
     * Finds trust tokens from an MFA `next` action.
     *
     * @param result The result from an MFA authentication step.
     * @return A [TrustSet] containing found tokens, or `null`.
     */
    internal fun findTrustTokens(result: MfaNext): TrustSet? {
        val passkeyAction = result.next?.firstOrNull {
            when (it.action.name) {
                MfaActionAction.Name.PASSKEY_COLON_REG,
                MfaActionAction.Name.PASSKEY_COLON_AUTH,
                MfaActionAction.Name.PASSKEY_COLON_TX -> true
                else -> false
            }
        } ?: return null

        val options = passkeyAction.options ?: return null

        val merchantTrustId = options.firstOrNull { it.name == MERCHANT_TRUST_ID }?.value
        val walletTrustId = options.firstOrNull { it.name == WALLET_TRUST_ID }?.value

        if (merchantTrustId == null && walletTrustId == null) {
            return null
        }

        return TrustSet(merchantTrustId, walletTrustId)
    }
}
