package io.loginid.core.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.StrongBoxUnavailableException
import io.loginid.core.extensions.base64URLEncode
import io.loginid.core.extensions.toUnsignedByteArray
import io.loginid.core.interfaces.KeyStoreManaging
import io.loginid.core.models.JWK
import java.security.KeyPair
import java.security.KeyStore
import java.security.KeyPairGenerator
import java.security.KeyStoreException
import java.security.PrivateKey
import java.security.ProviderException
import java.security.Signature
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec

/**
 * Manages cryptographic keys within the Android Keystore.
 * This class provides functionalities to generate, use, and manage ES256 key pairs.
 *
 * @property alias The alias used to identify the key pair in the Android Keystore.
 */
class KeyStoreManager(private val alias: String) : KeyStoreManaging {
    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }

    /**
     * Generates an ES256 (secp256r1) key pair using the Android KeyStore.
     * The private key is marked as non-authentication-required and attempts to use StrongBox if available.
     *
     * @return The generated [KeyPair].
     */
    override fun generateES256KeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            ANDROID_KEY_STORE,
        )

        return try {
            keyPairGenerator.initialize(createSpec(strongBox = true))
            keyPairGenerator.generateKeyPair()
        } catch (_: StrongBoxUnavailableException) {
            // StrongBox unavailable or failed; fall back to regular Keystore.
            keyPairGenerator.initialize(createSpec(strongBox = false))
            keyPairGenerator.generateKeyPair()
        } catch (_: ProviderException) {
            // StrongBox unavailable or failed; fall back to regular Keystore.
            keyPairGenerator.initialize(createSpec(strongBox = false))
            keyPairGenerator.generateKeyPair()
        }
    }

    /**
     * Signs the given data using the ES256 private key stored in the KeyStore.
     *
     * @param data The string data to be signed.
     * @return The Base64URL-encoded signature string, or null if signing fails.
     */
    override fun signWithES256PrivateKey(data: String): String? {
        return try {
            val keyStore = getKeyStore()
            val privateKey = keyStore.getKey(alias, null) as? PrivateKey
                ?: throw KeyStoreException("Private key not found for alias: $alias")

            val signature = Signature.getInstance("SHA256withECDSA")
            signature.initSign(privateKey)
            signature.update(data.toByteArray(Charsets.UTF_8))
            val derSignature = signature.sign()
            val rawSignature = CryptoUtils.derToConcatenatedRS(derSignature)
            rawSignature.base64URLEncode()
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Exports the public key associated with the alias in JWK (JSON Web Key) format.
     *
     * @return A [JWK] object representing the public key, or null if export fails.
     */
    override fun exportPublicKeyJwk(): JWK? {
        return try {
            val keyStore = getKeyStore()
            val publicKey = keyStore.getCertificate(alias)?.publicKey as? ECPublicKey
                ?: throw KeyStoreException("Public key not found for alias: $alias")

            val ecPoint = publicKey.w
            val xBytes = ecPoint.affineX.toUnsignedByteArray()
            val yBytes = ecPoint.affineY.toUnsignedByteArray()

            JWK(
                kty = "EC",
                crv = "P-256",
                x = xBytes.base64URLEncode(),
                y = yBytes.base64URLEncode(),
            )
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Deletes the key pair associated with the alias from the KeyStore.
     *
     * @return True if the key pair was found and deleted; false otherwise.
     */
    override fun deleteKeyPair(): Boolean {
        val keyStore = getKeyStore()
        return if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias)
            true
        } else {
            false
        }
    }

    /**
     * Checks if a key pair with the specified alias exists in the KeyStore.
     *
     * @return True if the key exists; false otherwise.
     */
    override fun hasKey(): Boolean {
        val keyStore = getKeyStore()
        return keyStore.containsAlias(alias)
    }

    /**
     * Retrieves an instance of the Android KeyStore.
     *
     * @return A loaded [KeyStore] instance.
     */
    private fun getKeyStore(): KeyStore {
        return KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
    }

    /**
     * Creates a [KeyGenParameterSpec] for generating an ES256 key pair.
     *
     * @param strongBox Indicates whether to request the key be stored in StrongBox.
     * @return A [KeyGenParameterSpec] configured for ES256 signing.
     */
    private fun createSpec(strongBox: Boolean): KeyGenParameterSpec =
        KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_SIGN,
        ).apply {
            setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
            setDigests(KeyProperties.DIGEST_SHA256)
            setUserAuthenticationRequired(false)
            setIsStrongBoxBacked(strongBox)
        }.build()
}
