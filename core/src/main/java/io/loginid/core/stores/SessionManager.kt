package io.loginid.core.stores

import io.loginid.core.interfaces.Storage
import io.loginid.core.interfaces.StorageKey
import io.loginid.core.models.LoginIDConfig
import io.loginid.core.models.LoginIDJWTAccess
import io.loginid.core.models.TokenSet
import com.squareup.moshi.Moshi

/**
 * Handles secure storage and retrieval of authentication tokens, scoped by a given App ID.
 *
 * @property storage The storage implementation to use for persisting the token set.
 * @param config The LoginID configuration, used to create a unique storage key.
 */
class SessionManager(
    private val storage: Storage,
    config: LoginIDConfig
) {
    private val tokenSetKey = StorageKey<String>("io.loginid.session.set.${config.getAppId()}")
    private val moshi = Moshi.Builder().build()
    private val tokenSetAdapter = moshi.adapter(TokenSet::class.java)

    /**
     * Logs the user out of the current session and removes any stored credentials or tokens.
     */
    suspend fun logout() {
        storage.delete(tokenSetKey)
    }

    /**
     * Persists a session's token set securely.
     *
     * @param tokenSet The [TokenSet] to store. If `null`, the method ignores the request.
     */
    suspend fun setTokenSet(tokenSet: TokenSet?) {
        tokenSet?.let {
            val tokenSetJson = tokenSetAdapter.toJson(it)
            storage.put(tokenSetKey, tokenSetJson)
        }
    }

    /**
     * Returns the authorization token to use for authenticated requests.
     *
     * If `authzToken` is provided and non-empty, it is returned directly.
     * Otherwise, the stored access token is returned if it exists and has not expired.
     *
     * @param authzToken An optional authorization token supplied by the caller.
     * @return A valid authorization token, or `null` if none is available.
     */
    suspend fun getAuthzToken(authzToken: String?): String? {
        if (!authzToken.isNullOrEmpty()) {
            return authzToken
        }
        return getAccessToken()
    }

    /**
     * Stores an access token in the current session.
     *
     * If a token set already exists, only its access token is updated. Otherwise,
     * a new token set is created containing just the access token.
     *
     * @param accessToken The access token to store. If `null`, the method ignores the request.
     */
    suspend fun setAccessToken(accessToken: String?) {
        accessToken?.let {
            val currentTokenSet = getTokenSet()
            val newTokenSet = currentTokenSet?.copy(accessToken = it) ?: TokenSet(accessToken = it)
            setTokenSet(newTokenSet)
        }
    }

    /**
     * Retrieves and validates the stored access token.
     *
     * The access token is decoded and its expiration time is checked.
     * If the token has expired or cannot be decoded, `null` is returned.
     *
     * @return A parsed [LoginIDJWTAccess] if the token is valid; otherwise `null`.
     */
    suspend fun getParsedAccessToken(): LoginIDJWTAccess? {
        val jwt = getTokenSet()?.accessToken ?: return null
        val decodedJWT = LoginIDJWTAccess.decodeToLoginIdToken(jwt) ?: return null

        val currentTime = System.currentTimeMillis() / 1000
        if (currentTime >= decodedJWT.exp) {
            return null
        }
        return decodedJWT
    }

    /**
     * Returns the stored access token if it exists and has not expired.
     *
     * @return A valid access token, or `null` if no valid token is available.
     */
    private suspend fun getAccessToken(): String? {
        return getParsedAccessToken()?.jwt
    }

    /**
     * Retrieves the stored token set from storage.
     *
     * @return The stored [TokenSet], or `null` if none exists or decoding fails.
     */
    private suspend fun getTokenSet(): TokenSet? {
        val tokenSetJson = storage.get(tokenSetKey) ?: return null
        return try {
            tokenSetAdapter.fromJson(tokenSetJson)
        } catch (e: Exception) {
            null
        }
    }
}
