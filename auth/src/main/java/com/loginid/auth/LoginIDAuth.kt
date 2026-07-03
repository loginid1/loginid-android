package com.loginid.auth

import android.app.Activity
import com.loginid.auth.controllers.Passkeys
import com.loginid.auth.models.AuthResult
import com.loginid.auth.models.CreatePasskeyOptions
import com.loginid.core.interfaces.Storage
import com.loginid.core.models.LoginIDConfig
import com.loginid.core.stores.DeviceStore
import com.loginid.core.stores.SharedPreferencesStorage
import com.loginid.core.utils.TaskHandler

class LoginIDAuth(config: LoginIDConfig) {
    private val masterStore: Storage = SharedPreferencesStorage(config.getContext())
    private val device = DeviceStore(masterStore, config)
    private val passkeysController = Passkeys(config, device)

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