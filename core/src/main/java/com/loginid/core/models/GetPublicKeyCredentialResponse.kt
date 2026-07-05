package com.loginid.core.models

import com.squareup.moshi.JsonClass

/**
 * Represents the response from a public key credential retrieval operation.
 * This class encapsulates the data returned by the authenticator after a successful authentication.
 *
 * @property response The core response data containing client data, authenticator data, and signature.
 * @property authenticatorAttachment The attachment modality of the authenticator.
 * @property id The base64url-encoded identifier of the retrieved credential.
 * @property rawId The raw identifier of the retrieved credential.
 * @property type The type of the credential, typically "public-key".
 */
@JsonClass(generateAdapter = true)
data class GetPublicKeyCredentialResponse(
    /**
     * The core response data containing client data, authenticator data, and signature.
     */
    val response: GetPublicKeyCredentialResponseResponse,

    /**
     * The attachment modality of the authenticator (e.g., "platform", "cross-platform").
     */
    val authenticatorAttachment: String,

    /**
     * The base64url-encoded identifier of the retrieved credential.
     */
    val id: String,

    /**
     * The raw identifier of the retrieved credential as a base64url-encoded string.
     */
    val rawId: String,

    /**
     * The type of the credential, typically "public-key".
     */
    val type: String,
)

/**
 * Contains the detailed response data from the authenticator for a credential retrieval operation.
 *
 * @property clientDataJSON A JSON string containing client data passed to the authenticator.
 * @property authenticatorData A base64url-encoded string containing authenticator data.
 * @property signature A base64url-encoded string of the signature produced by the authenticator.
 * @property userHandle An optional user handle associated with the credential.
 */
@JsonClass(generateAdapter = true)
data class GetPublicKeyCredentialResponseResponse(
    /**
     * A JSON string containing client data passed to the authenticator.
     */
    val clientDataJSON: String,

    /**
     * A base64url-encoded string containing authenticator data.
     */
    val authenticatorData: String,

    /**
     * A base64url-encoded string of the signature produced by the authenticator.
     */
    val signature: String,

    /**
     * An optional user handle associated with the credential.
     */
    val userHandle: String? = null,
)
