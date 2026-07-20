package com.loginid.core.models

import com.squareup.moshi.JsonClass
import io.loginid.client.model.CreationResult

/**
 * Represents the response from a public key credential creation operation.
 * This class encapsulates the data returned by the authenticator after a successful registration.
 *
 * @property response The core response data containing client data and attestation object.
 * @property authenticatorAttachment The attachment modality of the authenticator.
 * @property id The base64url-encoded identifier of the created credential.
 * @property rawId The raw identifier of the created credential.
 * @property type The type of the credential, typically "public-key".
 */
@JsonClass(generateAdapter = true)
data class CreatePublicKeyCredentialResponse(
    /**
     * The core response data containing client data and attestation object.
     */
    val response: CreatePublicKeyCredentialResponseResponse,

    /**
     * The attachment modality of the authenticator (e.g., "platform", "cross-platform").
     */
    val authenticatorAttachment: String?,

    /**
     * The base64url-encoded identifier of the created credential.
     */
    val id: String,

    /**
     * The raw identifier of the created credential as a base64url-encoded string.
     */
    val rawId: String,

    /**
     * The type of the credential, typically "public-key".
     */
    val type: String,
)

/**
 * Contains the detailed response data from the authenticator for a credential creation operation.
 *
 * @property clientDataJSON A JSON string containing client data passed to the authenticator.
 * @property attestationObject A base64url-encoded string containing the attestation object.
 * @property authenticatorData A base64url-encoded string containing authenticator data.
 * @property publicKeyAlgorithm The COSE algorithm identifier for the public key.
 * @property publicKey A base64url-encoded string of the public key.
 * @property transports A list of transport methods supported by the authenticator.
 */
@JsonClass(generateAdapter = true)
data class CreatePublicKeyCredentialResponseResponse(
    /**
     * A JSON string containing client data passed to the authenticator.
     */
    val clientDataJSON: String,

    /**
     * A base64url-encoded string containing the attestation object.
     */
    val attestationObject: String,

    /**
     * A base64url-encoded string containing authenticator data.
     */
    val authenticatorData: String?,

    /**
     * The COSE algorithm identifier for the public key.
     */
    val publicKeyAlgorithm: Long?,

    /**
     * A base64url-encoded string of the public key.
     */
    val publicKey: String?,

    /**
     * An optional list of transport methods supported by the authenticator (e.g., "internal", "usb", "nfc", "ble").
     */
    val transports: List<CreationResult.Transports>? = null
)
