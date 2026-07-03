package com.loginid.auth.controllers

import android.app.Activity
import com.loginid.auth.models.AuthResult
import com.loginid.auth.models.CreatePasskeyOptions
import com.loginid.core.models.LoginIDConfig
import com.loginid.core.stores.DeviceStore
import com.loginid.core.utils.Defaults
import com.loginid.core.utils.TrustID

internal class Passkeys(
    private val config: LoginIDConfig,
    private val device: DeviceStore,
    private val trustId: TrustID,
) {
    suspend fun createPasskey(
        activity: Activity,
        username: String,
        options: CreatePasskeyOptions? = null
    ): AuthResult {
        val appId = config.getAppId()
        val deviceInfo = Defaults.deviceInfo(activity, device, options?.deviceId)
        val trustItems = Defaults.trustItems(
            config = config,
            store = trustId,
            username = username
        )

        return AuthResult()
    }
}