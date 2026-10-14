package io.loginid.auth.models

import io.loginid.core.enums.UsernameType

/**
 * Options for authenticating with a passkey.
 *
 * @property usernameType The type of username validation to be used. Defaults to `[UsernameType.OTHER]`.
 * @property autofill When true, enables passkey keyboard autofill suggestions. Username does not need to be set.
 * @property traceId An optional identifier used to trace and correlate all events associated with this authentication interaction. If not provided, the server may generate one automatically.
 * @property deviceId An optional identifier for the device performing the authentication. Used to enable device-specific authentication flows and identify the device during future authentications. If not provided, the locally stored device ID is used.
 * @property txPayload An optional transaction payload used to initiate a non-repudiation signature process. When provided, the payload is embedded in the passkey challenge and signed by the user, providing evidence that the transaction (e.g. a payment or a change to account information) was approved by the passkey holder.
 * @property txNonce Optionally used with [txPayload]. A unique nonce that helps ensure the integrity of the transaction and prevents replay attacks.
 */
data class AuthenticateWithPasskeyOptions(
    val usernameType: UsernameType = UsernameType.OTHER,
    val autofill: Boolean? = null,
    val traceId: String? = null,
    val deviceId: String? = null,
    val txPayload: String? = null,
    val txNonce: String? = null
) {
    internal constructor(options: AuthenticateWithPasskeyOptions? = null) : this(
        usernameType = options?.usernameType ?: UsernameType.OTHER,
        autofill = true,
        traceId = options?.traceId,
        deviceId = options?.deviceId,
        txPayload = options?.txPayload,
        txNonce = options?.txNonce
    )
}
