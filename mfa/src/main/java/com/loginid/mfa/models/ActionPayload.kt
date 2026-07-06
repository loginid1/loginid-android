package com.loginid.mfa.models

/**
 * The final result after validating the MFA session info.
 *
 * @property session The MFA state session. This should be obtained from a previous MFA request or initiation step.
 * @property payload The payload required for completing the authentication factor. This typically contains user input or challenge-response data.
 */
internal data class ActionPayload(
    val session: String,
    val payload: String
)
