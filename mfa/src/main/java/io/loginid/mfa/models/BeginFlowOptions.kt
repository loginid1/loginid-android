package io.loginid.mfa.models

import io.loginid.core.enums.UsernameType

/**
 * Options for beginning Multi-Factor Authentication (MFA).
 *
 * @property displayName A human-palatable name for the user account, intended only for display on your passkeys and modals.
 * @property usernameType The type of username validation to be used. Defaults to `[UsernameType.OTHER]`.
 * @property txPayload A string representing transaction details for confirmation during MFA.
 * This can be plain text or a JSON-formatted string for structured details.
 * @property merchantTrustId Merchant-generated identifier for the current checkout session. Used as a key to retrieve associated trust information and link the session with wallet-issued identity data.
 * @property traceId A unique identifier used to trace and correlate all events associated with a single MFA interaction. If you don’t provide this, the server may generate one automatically.
 * @property deviceId An identifier for the device used in the authentication process. This property helps determine if
 * supported authentications can be proceeded, allowing future authentications to identify the device
 * correctly.
 * Overrides the stored device identifier. If not provided, the SDK uses the stored value.
 */
data class BeginFlowOptions(
    val displayName: String? = null,
    val usernameType: UsernameType = UsernameType.OTHER,
    val txPayload: String? = null,
    val merchantTrustId: String? = null,
    val traceId: String? = null,
    val deviceId: String? = null
)
