package com.loginid.core.extensions

import com.loginid.core.models.CreatePublicKeyCredentialResponse
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
        transports = this.response.transports,
        authenticatorData = this.response.authenticatorData,
        publicKeyAlgorithm = this.response.publicKeyAlgorithm,
        publicKey = this.response.publicKey,
    )
}
