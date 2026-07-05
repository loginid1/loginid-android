package com.loginid.auth

import android.app.Activity
import android.view.View
import com.loginid.auth.controllers.Passkeys
import com.loginid.auth.models.AuthResult
import com.loginid.auth.models.AuthenticateWithPasskeyOptions
import com.loginid.auth.models.ConfirmTransactionOptions
import com.loginid.auth.models.CreatePasskeyOptions
import com.loginid.auth.models.TxConfirmResult
import com.loginid.core.models.LoginIDConfig
import com.loginid.core.services.PasskeyService
import com.loginid.core.stores.DeviceStore
import com.loginid.core.stores.SessionManager
import com.loginid.core.stores.SharedPreferencesStorage
import com.loginid.core.utils.KeyStoreManager
import com.loginid.core.utils.PublicKeyManager
import com.loginid.core.utils.TaskHandler
import com.loginid.core.utils.TrustID

class LoginIDAuth(config: LoginIDConfig) {
    private val masterStore = SharedPreferencesStorage(config.getContext())
    private val session = SessionManager(masterStore, config)
    private val trustId = TrustID(
        config = config,
        storage = masterStore,
        keyStoreManagerFactory = { alias ->
            KeyStoreManager(alias)
        }
    )
    private val device = DeviceStore(masterStore, config)
    private val publicKeyManager = PublicKeyManager()
    private val passkeyApi = PasskeyService(config)
    private val passkeysController = Passkeys(
        config = config,
        device = device,
        session = session,
        trustId = trustId,
        passkeyApi = passkeyApi,
        publicKeyManager = publicKeyManager,
    )

    /**
     * This method helps to create a passkey. The only required parameter is the username, but additional attributes can be provided in the options parameter.
     * Note: While the authorization token is optional, it must always be used in a production environment. You can skip it during development by adjusting
     * the app configuration in the LoginID dashboard.
     *
     * A short-lived authorization token is returned, allowing access to protected resources for the given user such as listing, renaming or deleting passkeys.
     *
     * @param activity The current activity.
     * @param username The username for which to create the passkey.
     * @param options Optional parameters for passkey creation.
     * @return An [AuthResult] containing an authorization token.
     * @throws com.loginid.core.errors.LoginIDError if passkey creation fails.
     */
    suspend fun createPasskey(
        activity: Activity,
        username: String,
        options: CreatePasskeyOptions? = null
    ): AuthResult {
        return TaskHandler.executeTask {
            passkeysController.createPasskey(activity, username, options)
        }
    }

    /**
     * This method helps to create a passkey. The only required parameter is the username, but additional attributes can be provided in the options parameter.
     * Note: While the authorization token is optional, it must always be used in a production environment. You can skip it during development by adjusting
     * the app configuration in the LoginID dashboard.
     *
     * A short-lived authorization token is returned, allowing access to protected resources for the given user such as listing, renaming or deleting passkeys.
     *
     * @param activity The current activity.
     * @param username The username for which to create the passkey.
     * @param authzToken An authorization token from a previous authentication step.
     * @param options Optional parameters for passkey creation.
     * @return An [AuthResult] containing an authorization token.
     * @throws com.loginid.core.errors.LoginIDError if passkey creation fails.
     */
    suspend fun createPasskey(
        activity: Activity,
        username: String,
        authzToken: String,
        options: CreatePasskeyOptions? = null
    ): AuthResult {
        val opts = CreatePasskeyOptions(authzToken, options)
        return TaskHandler.executeTask {
            passkeysController.createPasskey(activity, username, opts)
        }
    }

    /**
     * This method authenticates a user with a passkey and may trigger additional browser dialogs to guide the user through the process.
     *
     * A short-lived authorization token is returned, allowing access to protected resources for the given user such as listing, renaming or deleting passkeys.
     *
     * @param activity The current activity.
     * @param username The username of the user to authenticate.
     * @param options Optional parameters for passkey authentication.
     * @return An [AuthResult] containing an authorization token if authentication is successful.
     * @throws com.loginid.core.errors.LoginIDError if authentication fails.
     */
    suspend fun authenticateWithPasskey(
        activity: Activity,
        username: String,
        options: AuthenticateWithPasskeyOptions? = null
    ): AuthResult {
        return TaskHandler.executeTask {
            passkeysController.authenticateWithPasskey(
                activity, username, null, options
            )
        }
    }

    /**
     * Authenticates a user by utilizing the browser's passkey autofill capabilities.
     *
     * A short-lived authorization token is returned, allowing access to protected resources for the given user such as listing, renaming or deleting passkeys.
     *
     * @param activity The current activity.
     * @param usernameAnchorView The view to which the passkey selection UI should be anchored. This is typically a username input field.
     * @param options Optional parameters for passkey authentication.
     * @return An [AuthResult] containing an authorization token if authentication is successful.
     * @throws com.loginid.core.errors.LoginIDError if authentication fails or is canceled.
     */
    suspend fun authenticateWithPasskeyAutofill(
        activity: Activity,
        usernameAnchorView: View,
        options: AuthenticateWithPasskeyOptions? = null
    ): AuthResult {
        val opts = AuthenticateWithPasskeyOptions(options)
        return TaskHandler.executeTask {
            passkeysController.authenticateWithPasskey(
                activity, "", usernameAnchorView, opts
            )
        }
    }

    /**
     * This method initiates a non-repudiation signature process by generating a transaction-specific challenge
     * and then expects the client to provide an assertion response using a passkey.
     *
     * This method is useful for confirming actions such as payments
     * or changes to sensitive account information, ensuring that the transaction is being authorized
     * by the rightful owner of the passkey.
     *
     * For a more detailed guide click [here](https://docs.loginid.io/user-scenario/authentication/step-up/transaction-confirmation/).
     *
     * @param activity The current activity.
     * @param username The username of the account holder confirming the transaction.
     * @param txPayload A string representing the transaction details, such as an amount or action.
     * @param options Optional parameters to customize the transaction, like providing a `nonce` or specifying the `txType`.
     * @return A [TxConfirmResult] containing a confirmation token and passkey details.
     * @throws com.loginid.core.errors.LoginIDError if the transaction confirmation fails or is canceled.
     */
    suspend fun confirmTransaction(
        activity: Activity,
        username: String,
        txPayload: String,
        options: ConfirmTransactionOptions? = null
    ): TxConfirmResult {
        return TaskHandler.executeTask {
            passkeysController.confirmTransaction(
                activity,
                username,
                txPayload,
                options
            )
        }
    }
}
