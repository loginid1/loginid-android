package com.loginid.core.interfaces

import android.app.Activity
import android.view.View
import com.loginid.core.models.CreatePublicKeyCredentialResponse
import com.loginid.core.models.GetPublicKeyCredentialResponse

/**
 * An interface for managing public key credential (passkey) operations.
 */
interface PublicKeyManaging {
    /**
     * Initiates the creation of a new public key credential.
     *
     * @param activity The current activity.
     * @param publicKey The public key credential creation options as a JSON string.
     * @return A [CreatePublicKeyCredentialResponse] upon successful creation.
     */
    suspend fun create(activity: Activity, publicKey: String): CreatePublicKeyCredentialResponse
    /**
     * Initiates the retrieval of an existing public key credential for authentication.
     *
     * @param activity The current activity.
     * @param publicKey The public key credential request options as a JSON string.
     * @param usernameAnchorView An optional view to anchor the credential manager UI for autofill.
     * @return A [GetPublicKeyCredentialResponse] upon successful retrieval.
     */
    suspend fun get(activity: Activity, publicKey: String, usernameAnchorView: View?): GetPublicKeyCredentialResponse
}
