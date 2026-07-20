package com.loginid.mfa.models

import io.loginid.client.model.PublicKeyCredentialCreationOptions
import io.loginid.client.model.PublicKeyCredentialRequestOptions

/**
 * Represents the type of passkey operation to perform,
 * either creating a new credential or requesting an existing one.
 */
internal sealed class PasskeyOptions {
    /**
     * Passkey creation using [PublicKeyCredentialCreationOptions].
     * @property options The public key credential creation options.
     */
    data class Creation(val options: PublicKeyCredentialCreationOptions) : PasskeyOptions()

    /**
     * Passkey assertion request using [PublicKeyCredentialRequestOptions].
     * @property options The public key credential request options.
     */
    data class Request(val options: PublicKeyCredentialRequestOptions) : PasskeyOptions()
}
