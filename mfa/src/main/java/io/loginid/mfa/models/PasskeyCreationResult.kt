package io.loginid.mfa.models

import io.loginid.client.model.CreationResult

/**
 * Represents the result of a passkey credential creation.
 *
 * @property attestationObject Base64URL-encoded attestation object bytes.
 * @property authenticatorAttachment The authenticator attachment modality: `"platform"` or `"cross-platform"`.
 * @property authenticatorData Base64URL-encoded authenticator data bytes.
 * @property clientDataJSON Base64URL-encoded UTF-8 JSON bytes representing the client data.
 * @property credentialId Base64URL-encoded credential ID bytes.
 * @property publicKey Base64URL-encoded DER SubjectPublicKeyInfo bytes.
 * @property publicKeyAlgorithm This operation returns the COSEAlgorithmIdentifier of the new credential.
 * @property transports These values are the transports that the authenticator is believed to support.
 */
data class PasskeyCreationResult(
    val attestationObject: String,
    val authenticatorAttachment: String?,
    val authenticatorData: String?,
    val clientDataJSON: String,
    val credentialId: String,
    val publicKey: String?,
    val publicKeyAlgorithm: Long?,
    val transports: List<String>?
) {
    /**
     * Initializes a `PasskeyCreationResult` from an auto-generated `CreationResult`.
     * @param result The auto-generated result object.
     */
    internal constructor(result: CreationResult) : this(
        attestationObject = result.attestationObject,
        authenticatorAttachment = result.authenticatorAttachment?.value,
        authenticatorData = result.authenticatorData,
        clientDataJSON = result.clientDataJSON,
        credentialId = result.credentialId,
        publicKey = result.publicKey,
        publicKeyAlgorithm = result.publicKeyAlgorithm,
        transports = result.transports?.map { it.name }
    )
}
