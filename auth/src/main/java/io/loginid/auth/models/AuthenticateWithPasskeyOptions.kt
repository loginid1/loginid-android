package io.loginid.auth.models

import io.loginid.core.enums.UsernameType

/**
 * Options for authenticating with a passkey.
 *
 * @property usernameType The type of username validation to be used. Defaults to `[UsernameType.OTHER]`.
 * @property autofill When true, enables passkey keyboard autofill suggestions. Username does not need to be set.
 * @property traceId An optional identifier used to trace and correlate all events associated with this authentication interaction. If not provided, the server may generate one automatically.
 */
data class AuthenticateWithPasskeyOptions(
    val usernameType: UsernameType = UsernameType.OTHER,
    val autofill: Boolean? = null,
    val traceId: String? = null
) {
    internal constructor(options: AuthenticateWithPasskeyOptions? = null) : this(
        usernameType = options?.usernameType ?: UsernameType.OTHER,
        autofill = true,
        traceId = options?.traceId
    )
}
