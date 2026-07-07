package com.loginid.wallet.auth

import android.content.Context
import com.loginid.core.errors.LoginIDError
import com.loginid.core.models.LoginIDConfig
import com.loginid.mfa.LoginIDMFA
import com.loginid.mfa.enums.ActionName
import com.loginid.mfa.models.BeginFlowOptions
import com.loginid.mfa.models.MFASessionResult
import com.loginid.mfa.models.PerformActionOptions

/**
 * A specialized authentication helper built on top of LoginID's MFA flow, designed for checkout scenarios
 * where you need both authentication and identity trust.
 *
 * This helps orchestrate the MFA flow tied to a transaction (e.g., confirming a purchase) using passkeys.
 */
class LoginIDWalletAuth {
    private val mfa: LoginIDMFA

    /**
     * Creates a new wallet authentication helper using a base URL.
     *
     * Use this initializer when you only have the environment base URL and want sensible defaults
     * for the underlying SDK configuration.
     *
     * @param context The Android application context.
     * @param baseUrl The LoginID environment base URL (e.g., from your environment configuration).
     */
    constructor(context: Context, baseUrl: String) {
        this.mfa = LoginIDMFA(LoginIDConfig(context = context, baseUrl = baseUrl))
    }

    /**
     * Creates a new wallet authentication helper with an explicit `LoginIDConfig`.
     *
     * Use this when you already have a fully-formed `LoginIDConfig` and want more control.
     *
     * @param config The complete configuration used to initialize the underlying LoginID SDK.
     */
    constructor(config: LoginIDConfig) {
        this.mfa = LoginIDMFA(config)
    }

    /**
     * Begins the MFA authentication flow for a checkout session.
     *
     * This starts an MFA session bound to a specific **transaction payload** and optional identifiers
     * such as merchant `checkoutId`.
     *
     * @param txPayload The transaction payload to be confirmed/authorized.
     * @param username The username of the user initiating MFA. Defaults to an empty string.
     * @param options Optional parameters for beginning the flow. If `options.txPayload` is provided,
     * it takes precedence over the `txPayload` parameter.
     *
     * @return An `MFASessionResult` describing the newly-started MFA session.
     *
     * @throws LoginIDError if validation fails (e.g., missing `txPayload`) or if the underlying SDK
     * encounters an error.
     */
    suspend fun beginFlow(
        txPayload: String,
        username: String = "",
        options: BeginFlowOptions? = null
    ): MFASessionResult {
        val finalTxPayload = options?.txPayload ?: txPayload

        if (finalTxPayload.isEmpty()) {
            throw LoginIDError(
                msgCode = "not_found",
                msg = "TX payload is required"
            )
        }

        val mfaOptions =
            options?.copy(txPayload = finalTxPayload) ?: BeginFlowOptions(txPayload = finalTxPayload)

        return mfa.beginFlow(
            username = username,
            options = mfaOptions
        )
    }

    /**
     * Performs an MFA action using the provided factor and optional payload.
     *
     * In a checkout context, this method commonly covers:
     * - **Passkey Registration (`PASSKEY_REG`)**: Register a new passkey.
     * - **Passkey Authentication (`PASSKEY_AUTH`)**: Sign into a wallet account using a passkey.
     * - **Passkey Transaction Confirmation (`PASSKEY_TX`)**: Confirm a specific transaction using a passkey.
     * - **External Authentication (`EXTERNAL`)**: Sign in with a third-party authenticator.
     *
     * @param action The MFA factor/action to execute.
     * @param options Action options such as `payload`, `displayName`, or an updated `txPayload`.
     *
     * @return An updated `MFASessionResult` after performing the requested action.
     *
     * @throws LoginIDError if the underlying SDK encounters an error.
     */
    suspend fun performAction(
        action: ActionName,
        options: PerformActionOptions? = null
    ): MFASessionResult {
        return mfa.performAction(
            action = action,
            options = options
        )
    }
}
