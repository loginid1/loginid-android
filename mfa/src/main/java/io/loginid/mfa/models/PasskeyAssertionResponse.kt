package io.loginid.mfa.models

import io.loginid.client.model.AuthenticatorAssertionResponse

/**
 * Represents the result of a passkey assertion.
 *
 * @property authenticatorData Base64URL-encoded authenticator data bytes.
 * @property clientDataJSON Base64URL-encoded UTF-8 JSON bytes representing the client data.
 * @property credentialId Base64URL-encoded credential ID bytes.
 * @property signature Base64URL-encoded signature bytes returned by the authenticator.
 * @property userHandle User handle returned from the authenticator.
 */
data class PasskeyAssertionResponse(
    val authenticatorData: String,
    val clientDataJSON: String,
    val credentialId: String,
    val signature: String,
    val userHandle: String?
) {
    /**
     * Initializes a `PasskeyAssertionResponse` from an auto-generated `AuthenticatorAssertionResponse`.
     * @param response The auto-generated response object.
     */
    internal constructor(response: AuthenticatorAssertionResponse) : this(
        authenticatorData = response.authenticatorData,
        clientDataJSON = response.clientDataJSON,
        credentialId = response.credentialId,
        signature = response.signature,
        userHandle = response.userHandle
    )
}
