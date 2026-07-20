package io.loginid.core.utils

import android.app.Activity
import android.view.View
import androidx.credentials.CreateCredentialResponse
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PendingGetCredentialRequest
import androidx.credentials.pendingGetCredentialRequest
import io.loginid.core.interfaces.PublicKeyManaging
import io.loginid.core.models.CreatePublicKeyCredentialResponse
import io.loginid.core.models.GetPublicKeyCredentialResponse
import com.squareup.moshi.Moshi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Manages public key credential operations using Android's Credential Manager.
 * This class handles the creation and retrieval of passkeys (public key credentials).
 */
class PublicKeyManager : PublicKeyManaging {
    private companion object {
        const val REGISTRATION_RESPONSE_BUNDLE_KEY =
            "androidx.credentials.BUNDLE_KEY_REGISTRATION_RESPONSE_JSON"
        const val AUTHENTICATION_RESPONSE_BUNDLE_KEY =
            "androidx.credentials.BUNDLE_KEY_AUTHENTICATION_RESPONSE_JSON"
    }

    /**
     * Initiates the creation of a new public key credential (passkey).
     *
     * @param activity The activity context to use for launching the credential manager UI.
     * @param publicKey The JSON string representation of the PublicKeyCredentialCreationOptions.
     * @return A [CreatePublicKeyCredentialResponse] containing the new credential information.
     */
    override suspend fun create(
        activity: Activity,
        publicKey: String
    ): CreatePublicKeyCredentialResponse {
        val credentialManager = CredentialManager.create(activity)
        val publicKeyCredentialRequest = CreatePublicKeyCredentialRequest(publicKey)
        val response = credentialManager.createCredential(activity, publicKeyCredentialRequest)
        return this.fromCreateCredentialResponse(response)
    }

    /**
     * Initiates the retrieval of an existing public key credential (passkey) for authentication.
     *
     * @param activity The activity context to use for launching the credential manager UI.
     * @param publicKey The JSON string representation of the PublicKeyCredentialRequestOptions.
     * @param usernameAnchorView An optional view to anchor the autofill UI to. If provided, it enables an autofill-like experience.
     * @return A [GetPublicKeyCredentialResponse] containing the credential information for authentication.
     */
    override suspend fun get(
        activity: Activity,
        publicKey: String,
        usernameAnchorView: View?
    ): GetPublicKeyCredentialResponse {
        val credentialManager = CredentialManager.create(activity)
        val getCredentialOption = GetPublicKeyCredentialOption(publicKey)
        val getCredentialRequest = GetCredentialRequest(listOf(getCredentialOption))

        // If usernameAnchorView is provided, we assume it is autofill
        if (usernameAnchorView != null) {
            return suspendCancellableCoroutine { continuation ->
                usernameAnchorView.pendingGetCredentialRequest =
                    PendingGetCredentialRequest(getCredentialRequest) { response ->
                        continuation.resume(fromGetCredentialResponse(response))
                    }
            }
        }

        val response = credentialManager.getCredential(activity, getCredentialRequest)
        return this.fromGetCredentialResponse(response)
    }

    /**
     * Converts a [CreateCredentialResponse] from the credential manager to a [CreatePublicKeyCredentialResponse].
     *
     * @param response The response object from the credential manager.
     * @return The parsed [CreatePublicKeyCredentialResponse].
     * @throws Exception if parsing fails.
     */
    private fun fromCreateCredentialResponse(
        response: CreateCredentialResponse
    ): CreatePublicKeyCredentialResponse {
        val json = response.data.getString(PublicKeyManager.REGISTRATION_RESPONSE_BUNDLE_KEY)
            ?: throw Exception("Unable to parse response")
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(CreatePublicKeyCredentialResponse::class.java)
        return jsonAdapter.fromJson(json) ?: throw Exception("Unable to parse response")
    }

    /**
     * Converts a [GetCredentialResponse] from the credential manager to a [GetPublicKeyCredentialResponse].
     *
     * @param response The response object from the credential manager.
     * @return The parsed [GetPublicKeyCredentialResponse].
     * @throws Exception if parsing fails.
     */
    private fun fromGetCredentialResponse(
        response: GetCredentialResponse
    ): GetPublicKeyCredentialResponse {
        val json = response.credential.data.getString(PublicKeyManager.AUTHENTICATION_RESPONSE_BUNDLE_KEY)
            ?: throw Exception("Unable to parse response")
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(GetPublicKeyCredentialResponse::class.java)
        return jsonAdapter.fromJson(json) ?: throw Exception("Unable to parse response")
    }
}
