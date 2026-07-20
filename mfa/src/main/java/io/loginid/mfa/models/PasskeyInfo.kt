package io.loginid.mfa.models

import io.loginid.client.model.AdditionalPasskeyInfo

/**
 * Represents additional information about a passkey.
 *
 * @property aaguid AAGUID identifying the passkey provider/authenticator model.
 * @property assertionResult The result of the passkey assertion.
 * @property creationResult The result of the passkey creation.
 * @property passkeyId Internal passkey ID which is used for [passkey management](https://docs.loginid.io/user-scenario/user-profile-management/passkey-management/).
 * @property publicKey Base64URL-encoded COSE public key of the passkey's credential.
 */
data class PasskeyInfo(
    val aaguid: String,
    val assertionResult: PasskeyAssertionResponse?,
    val creationResult: PasskeyCreationResult?,
    val passkeyId: String,
    val publicKey: String
) {
    /**
     * Initializes `PasskeyInfo` from an auto-generated `AdditionalPasskeyInfo`.
     * @param info The auto-generated info object.
     */
    internal constructor(info: AdditionalPasskeyInfo) : this(
        aaguid = info.aaguid,
        assertionResult = info.assertionResult?.let { PasskeyAssertionResponse(it) },
        creationResult = info.creationResult?.let { PasskeyCreationResult(it) },
        passkeyId = info.passkeyId,
        publicKey = info.publicKey
    )
}
