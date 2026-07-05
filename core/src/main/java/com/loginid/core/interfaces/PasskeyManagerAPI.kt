package com.loginid.core.interfaces

import com.loginid.client.model.Passkey

/**
 * An internal API for managing passkeys.
 */
interface PasskeyManagerAPI {
    /**
     * Lists all passkeys for the authorized user.
     *
     * @param authorization The authorization token.
     * @return A list of [Passkey] objects.
     */
    suspend fun listPasskeys(authorization: String?): List<Passkey>

    /**
     * Renames a passkey.
     *
     * @param id The ID of the passkey to rename.
     * @param name The new name for the passkey.
     * @param authorization The authorization token.
     */
    suspend fun renamePasskey(id: String, name: String, authorization: String?)

    /**
     * Deletes a passkey.
     *
     * @param id The ID of the passkey to delete.
     * @param authorization The authorization token.
     */
    suspend fun deletePasskey(id: String, authorization: String?)
}
