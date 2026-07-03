package com.loginid.auth

import android.app.Activity
import com.loginid.auth.controllers.Passkeys
import com.loginid.auth.models.AuthResult
import com.loginid.auth.models.CreatePasskeyOptions
import com.loginid.core.models.LoginIDConfig
import com.loginid.core.stores.DeviceStore
import com.loginid.core.stores.SharedPreferencesStorage
import com.loginid.core.utils.KeyStoreManager
import com.loginid.core.utils.TaskHandler
import com.loginid.core.utils.TrustID

class LoginIDAuth(config: LoginIDConfig) {
    private val masterStore = SharedPreferencesStorage(config.getContext())
    private val trustId = TrustID(
        config = config,
        storage = masterStore,
        keyStoreManagerFactory = { alias ->
            KeyStoreManager(alias)
        }
    )
    private val device = DeviceStore(masterStore, config)
    private val passkeysController = Passkeys(config, device, trustId)

    suspend fun createPasskey(
        activity: Activity,
        username: String,
        options: CreatePasskeyOptions? = null
    ): AuthResult {
        return TaskHandler.executeTask {
            passkeysController.createPasskey(activity, username, options)
        }
    }
}