package io.loginid.auth.controllers

import io.loginid.auth.models.LoginIDConfigResult
import io.loginid.auth.models.SessionInfo
import io.loginid.core.errors.LoginIDError
import io.loginid.core.interfaces.PasskeyAPI
import io.loginid.core.models.LoginIDConfig
import io.loginid.core.stores.DeviceStore
import io.loginid.core.stores.SessionManager
import io.loginid.core.utils.Defaults
import io.loginid.core.utils.DeviceUtils
import io.loginid.client.model.Application
import io.loginid.client.model.AuthInitRequestBody

internal class Utils(
    private val config: LoginIDConfig,
    private val passkeyApi: PasskeyAPI,
    private val session: SessionManager,
    private val device: DeviceStore,
) {
    suspend fun verifyConfigSettings(): LoginIDConfigResult? {
        if (config.getBaseUrl().isEmpty()) {
            return LoginIDConfigResult(
                solution = "Set the base URL with configure()",
                code = "not_found_base_url",
                errorMessage = "Base URL is not set"
            )
        }

        val appId: String
        try {
            appId = config.getAppId()
        } catch (e: IllegalArgumentException) {
            return LoginIDConfigResult(
                solution = "Please verify that your base URL is correct.",
                code = "invalid_app_id",
                errorMessage = "Invalid app ID"
            )
        }

        val user = Defaults.userLogin(username = "", usernameType = null)
        val deviceInfo = DeviceUtils.getDeviceInfo(config.getContext(), device.getDeviceId())

        val authInitRequestBody = AuthInitRequestBody(
            app = Application(id = appId),
            deviceInfo = deviceInfo,
            user = user
        )

        try {
            passkeyApi.authInit(request = authInitRequestBody, userAgent = null)
        } catch (e: Exception) {
            val err = if (e is LoginIDError) e else LoginIDError.unknownError()
            return LoginIDConfigResult(
                solution = "Verify that your application exists and the base URL is correct.",
                code = err.msgCode ?: "unknown",
                errorMessage = err.msg ?: "Unknown error"
            )
        }

        return null
    }

    suspend fun getSessionInfo(): SessionInfo? {
        val parsedToken = session.getParsedAccessToken() ?: return null
        return SessionInfo(parsedToken)
    }

    suspend fun logout() {
        session.logout()
    }
}
