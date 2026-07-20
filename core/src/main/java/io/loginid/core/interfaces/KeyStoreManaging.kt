package io.loginid.core.interfaces

import io.loginid.core.models.JWK
import java.security.KeyPair

/**
 * An interface for managing cryptographic keys within the Android Keystore.
 * This abstracts the functionalities to generate, use, and manage ES256 key pairs,
 * allowing for testable implementations.
 */
interface KeyStoreManaging {
    /**
     * Generates an ES256 (secp256r1) key pair using the Android KeyStore.
     * The private key is marked as non-authentication-required and attempts to use StrongBox if available.
     *
     * @return The generated [KeyPair].
     */
    fun generateES256KeyPair(): KeyPair

    /**
     * Signs the given data using the ES256 private key stored in the KeyStore.
     *
     * @param data The string data to be signed.
     * @return The Base64URL-encoded signature string, or null if signing fails.
     */
    fun signWithES256PrivateKey(data: String): String?

    /**
     * Exports the public key associated with the alias in JWK (JSON Web Key) format.
     *
     * @return A [JWK] object representing the public key, or null if export fails.
     */
    fun exportPublicKeyJwk(): JWK?

    /**
     * Deletes the key pair associated with the alias from the KeyStore.
     *
     * @return True if the key pair was found and deleted; false otherwise.
     */
    fun deleteKeyPair(): Boolean

    /**
     * Checks if a key pair with the specified alias exists in the KeyStore.
     *
     * @return True if the key exists; false otherwise.
     */
    fun hasKey(): Boolean
}
