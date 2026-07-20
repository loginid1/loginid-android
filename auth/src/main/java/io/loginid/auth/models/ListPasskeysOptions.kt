package io.loginid.auth.models

/**
 * Options for listing passkeys.
 *
 * @param authzToken An authorization token from a previous authentication step.
 */
data class ListPasskeysOptions(
    val authzToken: String
)
