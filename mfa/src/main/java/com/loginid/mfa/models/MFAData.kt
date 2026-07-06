package com.loginid.mfa.models

import com.loginid.core.models.TokenSet
import com.loginid.client.model.Mfa

/**
 * An extended set of tokens obtained upon login, with optional authentication details.
 *
 * @property tokenSet A collection of authentication tokens returned after a successful login.
 * @property passkeyInfo Represents additional information about a passkey.
 */
internal data class MFAData(
    val tokenSet: TokenSet,
    val passkeyInfo: PasskeyInfo?
) {
    /**
     * Initializes a `MFAData` from an auto-generated `Mfa`.
     * @param result The auto-generated result object.
     */
    internal constructor(result: Mfa) : this(
        tokenSet = TokenSet(result),
        passkeyInfo = result.passkeyInfo?.let { PasskeyInfo(it) }
    )
}
