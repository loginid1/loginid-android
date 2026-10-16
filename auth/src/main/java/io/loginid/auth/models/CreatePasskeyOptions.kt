package io.loginid.auth.models

import io.loginid.core.enums.UsernameType

/**
 * Creates a new set of options for passkey creation.
 *
 * @property authzToken An optional authorization token used for accessing protected resources.
 * @property usernameType The type of username validation to use. Defaults to `UsernameType.OTHER`.
 * @property displayName An optional human-readable name for the user account, displayed in passkeys and authentication prompts.
 * @property overrideDeviceDisplayName An optional value that overrides the username and display name shown by the authenticator during passkey creation. When set, `displayName` is ignored.
 * @property passkeyName An optional custom label or nickname for the passkey, useful for distinguishing between multiple passkeys.
 * @property deviceId An optional identifier for the device creating the passkey. Used to enable device-specific authentication flows and identify the device during future authentications.
 * @property traceId An optional identifier used to trace and correlate all events associated with this registration interaction. If not provided, the server may generate one automatically.
 */
data class CreatePasskeyOptions(
    val authzToken: String? = null,
    val usernameType: UsernameType = UsernameType.OTHER,
    val displayName: String? = null,
    val overrideDeviceDisplayName: String? = null,
    val passkeyName: String? = null,
    val deviceId: String? = null,
    val traceId: String? = null
) {
    internal constructor(authzToken: String?, options: CreatePasskeyOptions?) : this(
        authzToken = authzToken,
        usernameType = options?.usernameType ?: UsernameType.OTHER,
        displayName = options?.displayName,
        overrideDeviceDisplayName = options?.overrideDeviceDisplayName,
        passkeyName = options?.passkeyName,
        deviceId = options?.deviceId,
        traceId = options?.traceId
    )
}
