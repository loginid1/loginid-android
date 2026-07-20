package io.loginid.mfa.models

import io.loginid.client.model.MfaAction
import io.loginid.client.model.MfaActionAction
import io.loginid.mfa.enums.ActionName

/**
 * Represents an individual MFA (Multi-Factor Authentication) factor that the user must complete.
 *
 * @property type The type of the MFA factor, such as passkey or OTP via email or SMS.
 * Use this value in `performAction` to initiate the factor.
 * @property label A user-friendly label for the factor, providing context on how it should be used.
 * @property description A description of the MFA factor, explaining its purpose or instructions for completion.
 * @property value A unique token for authentication, useful for advanced MFA flows across multiple devices.
 * This is available for the `passkey` MFA factor.
 * Example: To authenticate or add a passkey on another device, pass this value
 * along with the session token to continue the MFA process.
 * @property options A list of available options for the MFA factor, if applicable.
 * Supported for `otp:email` and `otp:sms` MFA factors.
 * Typically includes valid email addresses or phone numbers for OTP delivery.
 */
data class RemainingAction(
    val type: ActionName,
    val label: String,
    val description: String?,
    val value: String?,
    val options: List<String>?
) {
    /**
     * Initializes `RemainingAction` from an auto-generated `MfaAction`.
     * @param factor The auto-generated action object.
     */
    internal constructor(factor: MfaAction) : this(
        type = ActionName.fromClientEnum(factor.action.name)!!,
        label = factor.action.label,
        description = factor.action.desc,
        value = factor.options?.let { opts ->
            when (factor.action.name) {
                MfaActionAction.Name.PASSKEY_COLON_REG, MfaActionAction.Name.PASSKEY_COLON_AUTH, MfaActionAction.Name.PASSKEY_COLON_TX ->
                    opts.firstOrNull { it.value?.isNotEmpty() == true }?.value
                else -> null
            }
        },
        options = factor.options?.let { opts ->
            if (factor.action.name == MfaActionAction.Name.OTP_COLON_SMS || factor.action.name == MfaActionAction.Name.OTP_COLON_EMAIL) {
                opts.mapNotNull { it.label }.filter { it.isNotEmpty() }.ifEmpty { null }
            } else {
                null
            }
        }
    )
}
