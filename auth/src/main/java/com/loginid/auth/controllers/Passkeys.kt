package com.loginid.auth.controllers

import android.app.Activity
import com.loginid.auth.extensions.fromJWT
import com.loginid.auth.models.AuthResult
import com.loginid.auth.models.CreatePasskeyOptions
import com.loginid.client.model.Application
import com.loginid.client.model.CreationResult
import com.loginid.client.model.RegCompleteRequestBody
import com.loginid.client.model.RegInitRequestBody
import com.loginid.core.extensions.toJSON
import com.loginid.core.interfaces.PasskeyAPI
import com.loginid.core.interfaces.PublicKeyManaging
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
    private val passkeyApi: PasskeyAPI,
    private val publicKeyManager: PublicKeyManaging
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

        val initRes = passkeyApi.regInit(
            request = regInitRequestBody,
            userAgent = userAgent,
            authorization = authzToken,
        )

        return invokePasskeyApi(initRes.session) {
            val credential = publicKeyManager.create(
                activity = activity,
                publicKey = initRes.registrationRequestOptions.toJSON()
            )

            val regCompleteRequestBody = RegCompleteRequestBody(
                creationResult = CreationResult(
                    attestationObject = credential.response.attestationObject,
                    clientDataJSON = credential.response.clientDataJSON,
                    credentialId = credential.id,
                    transports = credential.response.transports,
                    authenticatorData = credential.response.authenticatorData,
                    publicKeyAlgorithm = credential.response.publicKeyAlgorithm,
                    publicKey = credential.response.publicKey,
                ),
                session = initRes.session,
                passkeyName = options?.passkeyName,
            )

            val response = passkeyApi.regComplete(regCompleteRequestBody)

            session.setAccessToken(response.jwtAccess)
            device.setDeviceId(response.deviceId)

            AuthResult().fromJWT(response)
        }
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
