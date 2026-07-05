package com.loginid.auth.models

/**
 * Options for renaming a passkey.
 *
 * @param authzToken An authorization token from a previous authentication step.
 */
data class RenamePasskeyOptions(
    val authzToken: String
)
