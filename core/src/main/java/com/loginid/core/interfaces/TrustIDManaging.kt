package com.loginid.core.interfaces

import com.loginid.core.errors.LoginIDError

/**
 * A helper type that manages the lifecycle of a wallet identity using a cryptographic key stored in the Keystore.
 */
internal interface TrustIDManaging {

    /**
     * Deletes all of the stored wallet identity from the storage.
     */
    suspend fun deleteTrustId()

    /**
     * Signs a new or existing Trust ID using its associated private key.
     *
     * If a key pair is already stored, it is reused. Otherwise, a new key pair is generated and stored.
     *
     * @return A signed JWT representing the trust ID.
     * @throws LoginIDError for known LoginID API errors.
     */
    @Throws(LoginIDError::class)
    suspend fun signWithTrustId(): String

    /**
     * Signs a Trust ID for the specified username.
     *
     * If a Trust ID record already exists for the provided username,
     * the existing record is reused and its last-used timestamp is updated.
     * Otherwise, a new Trust ID record and key pair are created and stored.
     *
     * @param username The username associated with the Trust ID.
     *   If `null`, an anonymous Trust ID is used.
     * @return A signed JWT representing the Trust ID.
     * @throws LoginIDError for known LoginID API errors.
     */
    @Throws(LoginIDError::class)
    suspend fun signWithTrustId(username: String?): String
}