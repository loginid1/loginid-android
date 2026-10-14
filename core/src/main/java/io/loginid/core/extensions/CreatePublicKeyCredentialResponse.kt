package io.loginid.core.extensions

import io.loginid.core.models.CreatePublicKeyCredentialResponse
import io.loginid.client.model.CreationResult

/**
 * Converts a [CreatePublicKeyCredentialResponse] to a [CreationResult].
 *
 * This function maps the properties from the Android-specific credential response object
 * to the format required by the LoginID client API for completing passkey registration.
 *
 * @return A [CreationResult] instance populated with data from this response.
 */
fun CreatePublicKeyCredentialResponse.toCreationResult(): CreationResult {
    return CreationResult(
        attestationObject = this.response.attestationObject,
        clientDataJSON = this.response.clientDataJSON,
        credentialId = this.id,
        authenticatorAttachment = this.toAuthenticatorAttachment(),
        transports = this.response.transports,
        authenticatorData = this.response.authenticatorData,
        publicKeyAlgorithm = this.response.publicKeyAlgorithm,
        publicKey = this.response.publicKey,
    )
}

/**
 * Maps the attachment reported by Credential Manager to the API's attachment modality.
 *
 * @return The attachment modality, or `null` if the provider did not report it or reported an unknown value.
 */
private fun CreatePublicKeyCredentialResponse.toAuthenticatorAttachment(): CreationResult.AuthenticatorAttachment? {
    return CreationResult.AuthenticatorAttachment.entries.firstOrNull { it.value == this.authenticatorAttachment }
}
