package io.loginid.core.extensions

import io.loginid.client.model.RegInit

/**
 * Returns a copy of the [RegInit] response with any client-supplied overrides applied.
 *
 * @param deviceDisplayName When non-empty, replaces the `name` and `displayName` of the
 * registration user entity shown by the authenticator. Otherwise the response is left untouched.
 * @return The [RegInit] response, with overrides applied when defined.
 */
fun RegInit.applyingOverrides(deviceDisplayName: String? = null): RegInit {
    if (deviceDisplayName.isNullOrEmpty()) {
        return this
    }

    val options = registrationRequestOptions
    return copy(
        registrationRequestOptions = options.copy(
            user = options.user.copy(
                name = deviceDisplayName,
                displayName = deviceDisplayName
            )
        )
    )
}
