package io.loginid.auth.models

/**
 * Options for deleting a passkey.
 *
 * @param authzToken An authorization token from a previous authentication step.
 */
data class DeletePasskeyOptions(
    val authzToken: String
)
