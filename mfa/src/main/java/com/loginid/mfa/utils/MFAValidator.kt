package com.loginid.mfa.utils

import io.loginid.client.model.MfaAction
import io.loginid.client.model.MfaActionAction
import io.loginid.client.model.PublicKeyCredentialCreationOptions
import io.loginid.client.model.PublicKeyCredentialRequestOptions
import com.loginid.core.errors.LoginIDError
import com.loginid.core.extensions.decodeJWTOrPayloadSegment
import com.loginid.mfa.models.ActionPayload
import com.loginid.mfa.models.MFAInfo
import com.loginid.mfa.models.PasskeyOptions
import com.loginid.mfa.models.PerformActionOptions
import com.squareup.moshi.Moshi

/**
 * Utility for validating and resolving the required payload
 * to perform specific Multi-Factor Authentication (MFA) actions.
 */
internal object MFAValidator {
    private val moshi = Moshi.Builder().build()
    private val creationOptionsAdapter = moshi.adapter(PublicKeyCredentialCreationOptions::class.java)
    private val requestOptionsAdapter = moshi.adapter(PublicKeyCredentialRequestOptions::class.java)

    /**
     * Validates that all required information is present to perform the given MFA action,
     * and resolves the payload to be used.
     *
     * This method will:
     * 1. Ensure a session is available (from either `options` or `info`).
     * 2. Use an explicit payload from `options` if provided.
     * 3. Otherwise, attempt to extract the payload from the provided `MFAInfo` for supported actions.
     * 4. Throw a [LoginIDError] if required information cannot be found.
     *
     * @param action The MFA action to perform.
     * @param info Optional [MFAInfo] object containing available factors and options.
     * @param options Optional [PerformActionOptions] provided by the caller.
     * @return A fully populated [ActionPayload] instance.
     * @throws LoginIDError with `msgCode = "not_found"` if a session or payload is missing.
     * @throws LoginIDError if no matching factor is found for the given action.
     */
    internal fun actionValidator(
        action: MfaActionAction.Name,
        info: MFAInfo?,
        options: PerformActionOptions?
    ): ActionPayload {
        val session = options?.session ?: info?.session
        ?: throw LoginIDError("not_found", "A session is required to perform MFA factor.")

        if (options?.payload?.isNotEmpty() == true) {
            return ActionPayload(
                session = session,
                payload = options.payload,
            )
        }

        val canFindPayloadInInfo = setOf(
            MfaActionAction.Name.PASSKEY_COLON_REG,
            MfaActionAction.Name.PASSKEY_COLON_AUTH,
            MfaActionAction.Name.PASSKEY_COLON_TX,
            MfaActionAction.Name.OTP_COLON_EMAIL,
            MfaActionAction.Name.OTP_COLON_SMS
        )

        val next = info?.next
        if (next.isNullOrEmpty() || !canFindPayloadInInfo.contains(action)) {
            throw LoginIDError("not_found", "Payload is required to perform MFA factor.")
        }

        val factor = next.firstOrNull { it.action.name == action }
            ?: throw LoginIDError("not_found", "No matching factor found for ${action.value}.")

        val resolvedPayload: String = when (action) {
            MfaActionAction.Name.PASSKEY_COLON_REG,
            MfaActionAction.Name.PASSKEY_COLON_AUTH,
            MfaActionAction.Name.PASSKEY_COLON_TX -> getFactorPayload(action, factor)

            MfaActionAction.Name.OTP_COLON_EMAIL -> getFactorPayload(action, factor, "email:primary")

            MfaActionAction.Name.OTP_COLON_SMS -> getFactorPayload(action, factor)

            // Actions that must always be provided explicitly
            MfaActionAction.Name.OTP_COLON_VERIFY, MfaActionAction.Name.EXTERNAL -> throw LoginIDError(
                "not_found", "Payload is required to perform MFA factor."
            )
        }

        return ActionPayload(
            session = session,
            payload = resolvedPayload,
        )
    }

    /**
     * Validates a Base64URL-encoded passkey payload and returns the corresponding [PasskeyOptions].
     *
     * This method:
     * 1. Ensures the payload is non-empty.
     * 2. Decodes it from either a raw payload segment or a JWT.
     * 3. Attempts to parse it into a valid [PublicKeyCredentialCreationOptions] or [PublicKeyCredentialRequestOptions].
     *
     * @param payload The Base64URL-encoded payload string to validate.
     * @return A [PasskeyOptions] value representing either a creation or request operation.
     * @throws LoginIDError with `msgCode = "not_found"` if the payload is missing.
     * @throws LoginIDError with `msgCode = "invalid_param"` if decoding fails or the payload is invalid.
     */
    internal fun validatePasskeyPayload(payload: String?): PasskeyOptions {
        if (payload.isNullOrEmpty()) {
            throw LoginIDError("not_found", "Payload is required for passkeys")
        }

        val jsonDataString = payload.decodeJWTOrPayloadSegment()?.toString(Charsets.UTF_8)
            ?: throw LoginIDError("invalid_param", "Invalid payload for passkey")

        try {
            creationOptionsAdapter.fromJson(jsonDataString)?.let { creation ->
                if (creation.pubKeyCredParams.isNotEmpty()) {
                    return PasskeyOptions.Creation(creation)
                }
            }
        } catch (_: Exception) { /* Ignore and try next adapter */ }

        try {
            requestOptionsAdapter.fromJson(jsonDataString)?.let { request ->
                if (request.challenge.isNotEmpty()) {
                    return PasskeyOptions.Request(request)
                }
            }
        } catch (_: Exception) { /* Ignore */ }

        throw LoginIDError("invalid_param", "Invalid payload for passkey")
    }

    /**
     * Retrieves the payload for the given MFA factor, optionally filtering by key.
     *
     * @param action The MFA action being performed.
     * @param factor The [MfaAction] from which to retrieve the payload.
     * @param key For OTP actions, the contact key to match (e.g., `"email:primary"`). Defaults to `null`.
     * @return The resolved payload value.
     * @throws LoginIDError with `msgCode = "not_found"` if no valid payload is found.
     */
    private fun getFactorPayload(
        action: MfaActionAction.Name,
        factor: MfaAction,
        key: String? = null
    ): String {
        val options = factor.options
        if (options.isNullOrEmpty()) {
            throw LoginIDError("not_found", "Payload is required for ${action.value}.")
        }

        val isPasskey = setOf(
            MfaActionAction.Name.PASSKEY_COLON_REG,
            MfaActionAction.Name.PASSKEY_COLON_AUTH,
            MfaActionAction.Name.PASSKEY_COLON_TX
        )
        if (isPasskey.contains(action)) {
            // Passkeys: take the first option's value
            val value = options.firstOrNull()?.value
            if (!value.isNullOrEmpty()) {
                return value
            }
            throw LoginIDError("not_found", "Payload is required for ${action.value}.")
        }

        // OTP flows: choose by key if provided, else first option's label
        if (key != null) {
            val match = options.firstOrNull { it.name == key }
            val label = match?.label
            if (!label.isNullOrEmpty()) {
                return label
            }
            throw LoginIDError("not_found", "Contact is not found for ${action.value}.")
        } else {
            val label = options.firstOrNull()?.label
            if (!label.isNullOrEmpty()) {
                return label
            }
            throw LoginIDError("not_found", "Contact is not found for ${action.value}.")
        }
    }
}
