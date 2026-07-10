package com.loginid.auth

import android.app.Activity
import android.content.Context
import android.view.View
import com.loginid.auth.controllers.OTP
import com.loginid.auth.controllers.PasskeyManager
import com.loginid.auth.controllers.Passkeys
import com.loginid.auth.controllers.Utils
import com.loginid.auth.models.AuthResult
import com.loginid.auth.models.AuthenticateWithPasskeyOptions
import com.loginid.auth.models.ConfirmTransactionOptions
import com.loginid.auth.models.CreatePasskeyOptions
import com.loginid.auth.models.DeletePasskeyOptions
import com.loginid.auth.models.ListPasskeysOptions
import com.loginid.auth.models.LoginIDConfigResult
import com.loginid.auth.models.OTPResult
import com.loginid.auth.models.PasskeyDetails
import com.loginid.auth.models.RenamePasskeyOptions
import com.loginid.auth.models.RequestAndSendOtpOptions
import com.loginid.auth.models.RequestOtpOptions
import com.loginid.auth.models.SessionInfo
import com.loginid.auth.models.TxConfirmResult
import com.loginid.auth.models.ValidateOtpOptions
import com.loginid.core.enums.MessageMethod
import com.loginid.core.models.LoginIDConfig
import com.loginid.core.services.OTPService
import com.loginid.core.services.PasskeyManagerService
import com.loginid.core.services.PasskeyService
import com.loginid.core.stores.DeviceStore
import com.loginid.core.stores.SessionManager
import com.loginid.core.stores.SharedPreferencesStorage
import com.loginid.core.utils.KeyStoreManager
import com.loginid.core.utils.PublicKeyManager
import com.loginid.core.utils.TaskHandler
import com.loginid.core.utils.TrustID

/**
 * High-level entry point for LoginID authentication on Android.
 *
 * This facade composes core SDK services to provide a simple API for:
 * - Creating and authenticating with passkeys
 * - Confirming transactions (non-repudiation)
 * - Requesting, sending, and validating one-time passwords (OTP)
 * - Managing passkeys (list, rename, delete)
 * - Inspecting and clearing the current session
 * - Verifying environment configuration
 *
 * All public operations are exposed as suspend functions and should be called from a coroutine.
 * Instances are lightweight and can be stored as a singleton in your application layer.
 */
class LoginIDAuth(config: LoginIDConfig) {

    /**
     * Creates a new authentication helper using a base URL.
     *
     * Use this initializer when you only have the environment base URL and want sensible defaults
     * for the underlying SDK configuration.
     *
     * @param context The Android application context.
     * @param baseUrl The LoginID environment base URL (e.g., from your environment configuration).
     */
    constructor(context: Context, baseUrl: String) : this(LoginIDConfig(context = context, baseUrl = baseUrl))

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
    private val passkeyManagerApi = PasskeyManagerService(config)
    private val otpApi = OTPService(config)
    private val passkeyManager = PasskeyManager(
        session = session,
        passkeyManagerApi = passkeyManagerApi
    )
    private val otpController = OTP(
        session = session,
        otpApi = otpApi
    )
    private val passkeysController = Passkeys(
        config = config,
        device = device,
        session = session,
        trustId = trustId,
        passkeyApi = passkeyApi,
        publicKeyManager = publicKeyManager,
    )
    private val utilsController = Utils(
        config = config,
        passkeyApi = passkeyApi,
        session = session,
        device = device
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

    /**
     * This method returns list of passkeys associated with the current user. The user must be fully authorized for this call to succeed.
     *
     * @param options Options for the request, including an optional authorization token.
     * @return An array of [PasskeyDetails] objects.
     * @throws com.loginid.core.errors.LoginIDError if the user is not authenticated or the request fails.
     */
    suspend fun listPasskeys(options: ListPasskeysOptions? = null): List<PasskeyDetails> {
        return TaskHandler.executeTask {
            passkeyManager.listPasskeys(options)
        }
    }

    /**
     * This method returns list of passkeys associated with the current user. The user must be fully authorized for this call to succeed.
     *
     * @param authzToken Authorization token.
     * @return An array of [PasskeyDetails] objects.
     * @throws com.loginid.core.errors.LoginIDError if the user is not authenticated or the request fails.
     */
    suspend fun listPasskeys(authzToken: String): List<PasskeyDetails> {
        val opts = ListPasskeysOptions(authzToken)
        return TaskHandler.executeTask {
            passkeyManager.listPasskeys(opts)
        }
    }

    /**
     * Renames a specified passkey by ID. The user must be fully authorized for this call to succeed.
     *
     * @param id The unique identifier of the passkey to rename.
     * @param name The new name for the passkey.
     * @param options Options for the request, including an optional authorization token.
     * @throws com.loginid.core.errors.LoginIDError if the user is not authenticated or the request fails.
     */
    suspend fun renamePasskey(id: String, name: String, options: RenamePasskeyOptions? = null) {
        return TaskHandler.executeTask {
            passkeyManager.renamePasskey(id, name, options)
        }
    }

    /**
     * Renames a specified passkey by ID. The user must be fully authorized for this call to succeed.
     *
     * @param id The unique identifier of the passkey to rename.
     * @param name The new name for the passkey.
     * @param authzToken Authorization token.
     * @throws com.loginid.core.errors.LoginIDError if the user is not authenticated or the request fails.
     */
    suspend fun renamePasskey(id: String, name: String, authzToken: String) {
        val opts = RenamePasskeyOptions(authzToken)
        return TaskHandler.executeTask {
            passkeyManager.renamePasskey(id, name, opts)
        }
    }

    /**
     * Delete a specified passkey by ID from LoginID. The user must be fully authorized for this call to succeed.
     *
     * @param id The unique identifier of the passkey to delete.
     * @param options Options for the request, including an optional authorization token.
     * @throws com.loginid.core.errors.LoginIDError if the user is not authenticated or the request fails.
     */
    suspend fun deletePasskey(id: String, options: DeletePasskeyOptions? = null) {
        return TaskHandler.executeTask {
            passkeyManager.deletePasskey(id, options)
        }
    }

    /**
     * Delete a specified passkey by ID from LoginID. The user must be fully authorized for this call to succeed.
     *
     * @param id The unique identifier of the passkey to delete.
     * @param authzToken Authorization token.
     * @throws com.loginid.core.errors.LoginIDError if the user is not authenticated or the request fails.
     */
    suspend fun deletePasskey(id: String, authzToken: String) {
        val opts = DeletePasskeyOptions(authzToken)
        return TaskHandler.executeTask {
            passkeyManager.deletePasskey(id, opts)
        }
    }

    /**
     * This method returns a one-time OTP to be displayed on the current device. The user must be authenticated on this device. The OTP is meant for cross-authentication, where the user reads the OTP from the screen and enters it on the target device.
     *
     * @param options Options for the request, including an optional authorization token.
     * @return An [OTPResult] containing the generated code and its expiration details.
     * @throws com.loginid.core.errors.LoginIDError if the request fails.
     */
    suspend fun requestOtp(options: RequestOtpOptions? = null): OTPResult {
        return TaskHandler.executeTask {
            otpController.requestOtp(options)
        }
    }

    /**
     * This method verifies the OTP and returns an authorization token, which can be used with the `createPasskey()`
     * method to create a new passkey. The authorization token has a short validity period and should be used immediately.
     *
     * @param username The username of the account being authenticated.
     * @param otp The one-time password entered by the user.
     * @param options Optional parameters to customize the request, such as `usernameType`.
     * @return An [AuthResult] with tokens if the OTP is valid.
     * @throws com.loginid.core.errors.LoginIDError if validation fails.
     */
    suspend fun validateOtp(
        username: String,
        otp: String,
        options: ValidateOtpOptions? = null
    ): AuthResult {
        return TaskHandler.executeTask {
            otpController.validateOtp(username, otp, options)
        }
    }

    /**
     * This method requests an OTP from the backend to be sent via the selected method. The method of delivery should be based on
     * the user's choice from the list of available options. This can be found in the result of `authenticateWithPasskey`
     * method as `fallbackOptions`.
     *
     * @param username The username to send the OTP to.
     * @param method The delivery channel, either `EMAIL` or `SMS`. Defaults to `EMAIL`.
     * @param options Optional parameters to customize the request, such as `usernameType`.
     * @throws com.loginid.core.errors.LoginIDError if the OTP cannot be sent.
     */
    suspend fun requestAndSendOtp(
        username: String,
        method: MessageMethod = MessageMethod.EMAIL,
        options: RequestAndSendOtpOptions? = null
    ) {
        return TaskHandler.executeTask {
            otpController.requestAndSendOtp(username, method, options)
        }
    }

    /**
     * Validates the application's configuration settings and provides a suggested correction if any issues are detected.
     *
     * @return A [LoginIDConfigResult] if there is a configuration issue, otherwise `null`.
     */
    suspend fun verifyConfigSettings(): LoginIDConfigResult? {
        return TaskHandler.executeTask {
            utilsController.verifyConfigSettings()
        }
    }

    /**
     * Check whether the user of the current session is authenticated and returns user info.
     * This info is retrieved locally and no requests to backend are made.
     *
     * @return A [SessionInfo] object if a valid session exists, otherwise `null`.
     */
    suspend fun getSessionInfo(): SessionInfo? {
        return TaskHandler.executeTask {
            utilsController.getSessionInfo()
        }
    }

    /**
     * Clears current user session. This method deletes the authorization token locally.
     */
    suspend fun logout() {
        TaskHandler.executeTask {
            utilsController.logout()
        }
    }
}
