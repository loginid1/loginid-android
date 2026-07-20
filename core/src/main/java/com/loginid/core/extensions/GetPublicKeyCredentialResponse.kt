package com.loginid.core.extensions

import com.loginid.core.models.GetPublicKeyCredentialResponse
import io.loginid.client.model.AuthenticatorAssertionResponse

/**
 * Converts a [GetPublicKeyCredentialResponse] to an [AuthenticatorAssertionResponse].
 *
 * This function maps the properties from the Android-specific credential response object
 * to the format required by the LoginID client API for completing passkey authentication
 * or transaction confirmation.
 *
 * @return An [AuthenticatorAssertionResponse] instance populated with data from this response.
 */
fun GetPublicKeyCredentialResponse.toAuthenticatorAssertionResponse(): AuthenticatorAssertionResponse {
    return AuthenticatorAssertionResponse(
        authenticatorData = this.response.authenticatorData,
        clientDataJSON = this.response.clientDataJSON,
        credentialId = this.id,
        signature = this.response.signature,
        userHandle = this.response.userHandle
    )
}
