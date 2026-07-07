package com.loginid.mfa

import android.app.Activity
import com.loginid.core.errors.LoginIDError
import com.loginid.core.models.LoginIDConfig
import com.loginid.core.services.MFAService
import com.loginid.core.stores.DeviceStore
import com.loginid.core.stores.SessionManager
import com.loginid.core.stores.SharedPreferencesStorage
import com.loginid.core.utils.KeyStoreManager
import com.loginid.core.utils.PublicKeyManager
import com.loginid.core.utils.TaskHandler
import com.loginid.core.utils.TrustID
import com.loginid.mfa.controllers.MFA
import com.loginid.mfa.enums.ActionName
import com.loginid.mfa.models.BeginFlowOptions
import com.loginid.mfa.models.MFASessionResult
import com.loginid.mfa.models.PerformActionOptions
import com.loginid.mfa.stores.MFAStore

class LoginIDMFA(config: LoginIDConfig) {
    private val masterStore = SharedPreferencesStorage(config.getContext())
    private val session = SessionManager(masterStore, config)
    private val store = MFAStore(masterStore, config)
    private val trustId = TrustID(
        config = config,
        storage = masterStore,
        keyStoreManagerFactory = { alias ->
            KeyStoreManager(alias)
        }
    )
    private val device = DeviceStore(masterStore, config)
    private val publicKeyManager = PublicKeyManager()
    private val mfaApi = MFAService(config)
    private val mfaController = MFA(
        config = config,
        device = device,
        session = session,
        store = store,
        trustId = trustId,
        mfaApi = mfaApi,
        publicKeyManager = publicKeyManager
    )

    /**
     * Initiates the pre-authentication process for Multi-Factor Authentication (MFA).
     *
     * Begins an MFA session and persists session details to secure storage.
     *
     * @param username The username of the user initiating MFA.
     * @param options Optional parameters for initiating MFA. Pass `null` to use defaults.
     * @return An `MFASessionResult` describing the current MFA session state and next actions.
     * @throws com.loginid.core.errors.LoginIDError if the request fails or the response is invalid.
     */
    suspend fun beginFlow(
        username: String,
        options: BeginFlowOptions? = null
    ): MFASessionResult {
        return TaskHandler.executeTask {
            mfaController.beginFlow(username, options)
        }
    }

    /**
     * Performs a Multi-Factor Authentication (MFA) action using the specified factor.
     *
     * Supports passkeys, OTP (email/SMS), and external authentication. Validates the
     * provided options, processes the step, calls the corresponding MFA API, and updates
     * the MFA session details upon success.
     *
     * **Important:**
     * - **OTP Request (email/SMS):** Sends an OTP to the user's contact. If `options.payload`
     *   contains a contact, it is used; otherwise, the primary contact on record is used.
     * - **OTP Verify (email/SMS):** Verifies the OTP code provided in `options.payload`.
     * - **External authentication:** Provide the authorization code in `options.payload`.
     * - **Passkeys:** Uses WebAuthn for authentication or registration.
     *
     * @param action The MFA factor to perform (e.g., `MFAActionName.PASSKEY_AUTH`).
     * @param options The session/payload options for the action. Pass `null` to resolve defaults when possible.
     * @return An updated `MFASessionResult` reflecting the new MFA state and next actions.
     * @throws com.loginid.core.errors.LoginIDError if validation fails, the payload is missing/invalid, or the API call fails.
     */
    suspend fun performAction(
        action: ActionName,
        options: PerformActionOptions? = null
    ): MFASessionResult {
        return TaskHandler.executeTask {
            mfaController.performAction(action, options)
        }
    }

    /**
     * Performs a passkey-based Multi-Factor Authentication (MFA) action.
     *
     * This is a convenience method for passkey operations that require an `Activity`.
     *
     * @param action The MFA factor to perform. Must be one of `PASSKEY_REG`, `PASSKEY_AUTH`, or `PASSKEY_TX`.
     * @param activity The `Activity` required for passkey operations.
     * @param options Optional parameters for the action.
     * @return An updated `MFASessionResult` reflecting the new MFA state and next actions.
     * @throws com.loginid.core.errors.LoginIDError if an unsupported action is provided.
     */
    suspend fun performAction(
        action: ActionName,
        activity: Activity,
        options: PerformActionOptions? = null
    ): MFASessionResult {
        val supportedActions = setOf(
            ActionName.PASSKEY_REG,
            ActionName.PASSKEY_AUTH,
            ActionName.PASSKEY_TX
        )
        if (action !in supportedActions) {
            throw LoginIDError(
                msgCode = "not_supported",
                msg = "MFA factor $action is not supported for this operation. Only passkey operations are supported."
            )
        }
        val finalOptions = options?.copy(activity = activity) ?: PerformActionOptions(activity = activity)
        return performAction(action, finalOptions)
    }
}
