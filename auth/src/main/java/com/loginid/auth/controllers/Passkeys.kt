package com.loginid.auth.controllers

import android.app.Activity
import com.loginid.auth.models.AuthResult
import com.loginid.auth.models.CreatePasskeyOptions
import com.loginid.client.model.Application
import com.loginid.client.model.RegInitRequestBody
import com.loginid.core.models.LoginIDConfig
import com.loginid.core.stores.DeviceStore
import com.loginid.core.stores.SessionManager
import com.loginid.core.utils.Defaults
import com.loginid.core.utils.DeviceUtils
import com.loginid.core.utils.TrustID

internal class Passkeys(
    private val config: LoginIDConfig,
    private val device: DeviceStore,
    private val session: SessionManager,
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
        val user = Defaults.user(
            displayName = options?.displayName,
            username = username,
            usernameType = options?.usernameType
        )

        val regInitRequestBody = RegInitRequestBody(
            app = Application(appId),
            deviceInfo = deviceInfo,
            user = user,
            trustItems = trustItems
        )

        val userAgent = DeviceUtils.getUserAgent()
        val authzToken = session.getAuthzToken(options?.authzToken)

        return AuthResult()
    }

    private suspend fun <T> invokePasskeyApi(
        session: String,
        fn: suspend () -> T
    ): T {
        return try {
            fn()
        } catch (e: Exception) {
            // TODO: clientEvents.submitASAuthorizationErrorEvent
            throw e
        }
    }
}
