package io.loginid.mfa.models

import io.loginid.client.model.MfaNext
import io.loginid.core.extensions.firstMatch
import io.loginid.mfa.enums.ActionName

/**
 * Represents the result of a Multi-Factor Authentication (MFA) session.
 *
 * This structure is used to track the status of an ongoing MFA process, including
 * remaining factors, user details, and issued authentication tokens.
 *
 * @property flow The MFA flow type indicating whether the session is part of sign-in or sign-up. This helps differentiate between authentication scenarios.
 * @property remainingFactors List of MFA factors that still need to be completed for authentication. If this list is empty, the authentication process is complete.
 * @property username The username associated with the authentication session. This may be `null` if not provided or applicable.
 * @property isComplete Indicates whether the MFA session is complete. If `true`, all required factors have been successfully validated.
 * @property session The MFA state session. This should be obtained from a previous MFA request or initiation step.
 * @property idToken A JSON Web Token (JWT) issued upon successful authentication. Used to verify user identity and grant access to protected resources.
 * @property accessToken A JSON Web Token (JWT) used for authorizing API requests. This token grants access to user-specific resources and actions.
 * @property refreshToken A token used to obtain new access and ID tokens after expiration. This helps maintain user sessions without requiring re-authentication.
 * @property payloadSignature A JSON Web Signature (JWS) that provides cryptographic proof of the payload's integrity. Ensures that the authentication data has not been tampered with.
 * @property merchantTrustId A signed trust token representing a trusted relationship between the merchant and the user's wallet. This value is issued by LoginID after the merchant trust relationship has been successfully verified during a checkout authentication flow. The value may be verified by backend services using LoginID [token verification](https://docs.loginid.io/backend-integration/verify-token). Available only after the MFA session is complete.
 * @property walletTrustId A signed trust token representing a trusted wallet. This value is issued by LoginID after the wallet trust relationship has been successfully verified during a checkout authentication flow. The value may be verified by backend services using LoginID [token verification](https://docs.loginid.io/backend-integration/verify-token). Available only after the MFA session is complete.
 * @property passkeyInfo Information about the passkey involved in this MFA session, including passkey creation or authentication results when applicable. Available only after the MFA session is complete.
 * @property deviceId An identifier for the device used in the authentication process. Available only after the MFA session is complete.
 * @property nextAction The next recommended MFA factor action to take. Indicates which MFA factor the user should complete next in order to proceed.
 */
data class MFASessionResult(
    val flow: MfaNext.Flow?,
    val remainingFactors: List<RemainingAction>,
    val username: String?,
    val isComplete: Boolean,
    val session: String?,
    val idToken: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val payloadSignature: String?,
    val merchantTrustId: String?,
    val walletTrustId: String?,
    val passkeyInfo: PasskeyInfo?,
    val deviceId: String?,
    val nextAction: ActionName?
) {
    companion object {
        /**
         * Creates a new instance of `MFASessionResult` using optional `MFAInfo`, `MFAData`, and `TrustSet` data.
         *
         * This function:
         * - Copies flow, username, session, and token values from the provided parameters.
         * - Determines if the MFA process is complete based on token availability.
         * - Selects the next action to perform based on a predefined priority list.
         * - Builds a list of remaining factors with relevant details and options.
         *
         * @param info Optional `MFAInfo` containing current MFA state and available actions.
         * @param mfaData Optional `MFAData` containing authentication tokens and passkey information.
         * @param trustSet Optional `TrustSet` containing checkout trust tokens from LoginID.
         * @return A new `MFASessionResult` instance.
         */
        internal fun from(
            info: MFAInfo?,
            mfaData: MFAData?,
            trustSet: TrustSet?
        ): MFASessionResult {
            val isComplete = (mfaData?.tokenSet?.accessToken != null || mfaData?.tokenSet?.payloadSignature != null)
            val supportedNext = info?.next?.filter {
                ActionName.fromClientEnum(it.action.name) != null
            }
            return MFASessionResult(
                flow = info?.flow,
                remainingFactors = supportedNext?.map { RemainingAction(it) } ?: emptyList(),
                username = info?.username,
                isComplete = isComplete,
                session = info?.session,
                idToken = mfaData?.tokenSet?.idToken,
                accessToken = mfaData?.tokenSet?.accessToken,
                refreshToken = mfaData?.tokenSet?.refreshToken,
                payloadSignature = mfaData?.tokenSet?.payloadSignature,
                merchantTrustId = trustSet?.merchantTrustId,
                walletTrustId = trustSet?.walletTrustId,
                passkeyInfo = if (isComplete) mfaData?.passkeyInfo else null,
                deviceId = if (isComplete) mfaData?.deviceId else null,
                nextAction = supportedNext?.mapNotNull { ActionName.fromClientEnum(it.action.name) }?.firstMatch(
                    ordered = listOf(
                        ActionName.PASSKEY_REG, ActionName.PASSKEY_AUTH, ActionName.PASSKEY_TX,
                        ActionName.OTP_SMS, ActionName.OTP_EMAIL, ActionName.EXTERNAL
                    )
                )
            )
        }
    }
}
