package com.loginid.core.models

import com.loginid.core.extensions.base64URLDecode
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

/**
 * Represents the decoded payload of a LoginID access token (JWT).
 *
 * This type provides access to the standard JWT claims used by LoginID.
 * The original JWT string is available through the [jwt] property after
 * decoding with [decodeToLoginIdToken].
 *
 * @property username The authenticated user's username.
 * @property aud The intended audience of the token.
 * @property iss The issuer of the token.
 * @property sub The subject identifier for the authenticated user.
 * @property exp The token expiration time, expressed as seconds since the Unix epoch.
 * @property rpId Relying party ID used typically used for passkey authentication to confirm the user has signed in under the specific domain.
 */
@JsonClass(generateAdapter = true)
data class LoginIDJWTAccess(
    val username: String,
    val aud: String,
    val iss: String,
    val sub: String,
    val exp: Long,
    val rpId: String
) {
    /**
     * The original encoded JWT.
     *
     * This value is not part of the JWT payload. It is populated after a
     * successful call to [decodeToLoginIdToken].
     */
    @Transient
    var jwt: String? = null

    companion object {
        private val moshi by lazy { Moshi.Builder().build() }
        private val jsonAdapter by lazy { moshi.adapter(LoginIDJWTAccess::class.java) }

        /**
         * Decodes a LoginID JWT into a [LoginIDJWTAccess] instance.
         *
         * The JWT payload is Base64URL-decoded and deserialized into a
         * [LoginIDJWTAccess]. If decoding succeeds, the original JWT string is
         * stored in the returned instance's [jwt] property.
         *
         * @param jwt The encoded JWT string.
         * @return A decoded [LoginIDJWTAccess] instance, or `null` if the JWT
         *   is malformed or its payload cannot be decoded.
         */
        fun decodeToLoginIdToken(jwt: String): LoginIDJWTAccess? {
            val segments = jwt.split('.')
            if (segments.size != 3) {
                return null
            }

            val payloadData = segments[1].base64URLDecode() ?: return null
            val payloadString = String(payloadData, Charsets.UTF_8)

            return try {
                jsonAdapter.fromJson(payloadString)?.apply {
                    this.jwt = jwt
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
