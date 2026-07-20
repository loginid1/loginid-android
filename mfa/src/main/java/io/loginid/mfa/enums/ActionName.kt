package io.loginid.mfa.enums

import io.loginid.client.model.MfaActionAction

/**
 * Represents the type of MFA (Multi-Factor Authentication) action to be performed.
 */
enum class ActionName(internal val clientEnum: MfaActionAction.Name) {

    /**
     * Passkey registration.
     */
    PASSKEY_REG(MfaActionAction.Name.PASSKEY_COLON_REG),

    /**
     * Passkey authentication.
     */
    PASSKEY_AUTH(MfaActionAction.Name.PASSKEY_COLON_AUTH),

    /**
     * Passkey transaction confirmation.
     */
    PASSKEY_TX(MfaActionAction.Name.PASSKEY_COLON_TX),

    /**
     * One-Time Password (OTP) via SMS. Used to request an OTP.
     */
    OTP_SMS(MfaActionAction.Name.OTP_COLON_SMS),

    /**
     * One-Time Password (OTP) via email. Used to request an OTP.
     */
    OTP_EMAIL(MfaActionAction.Name.OTP_COLON_EMAIL),

    /**
     * One-Time Password (OTP) verification. Used to verify an OTP code from any channel.
     */
    OTP_VERIFY(MfaActionAction.Name.OTP_COLON_VERIFY),

    /**
     * External or third-party authentication.
     */
    EXTERNAL(MfaActionAction.Name.EXTERNAL);

    internal companion object {
        private val map = entries.associateBy { it.clientEnum }
        internal fun fromClientEnum(clientEnum: MfaActionAction.Name): ActionName? = map[clientEnum]
    }
}
