package com.loginid.auth.models

import com.loginid.core.enums.UsernameType

/**
 * Options for authenticating with a passkey.
 *
 * @property usernameType The type of username validation to be used. Defaults to `[UsernameType.OTHER]`.
 * @property autofill When true, enables passkey keyboard autofill suggestions. Username does not need to be set.
 */
data class AuthenticateWithPasskeyOptions(
    val usernameType: UsernameType = UsernameType.OTHER,
    val autofill: Boolean? = null
) {
    internal constructor(options: AuthenticateWithPasskeyOptions? = null) : this(
        usernameType = options?.usernameType ?: UsernameType.OTHER,
        autofill = true
    )
}
