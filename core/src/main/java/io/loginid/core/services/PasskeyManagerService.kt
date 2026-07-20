package io.loginid.core.services

import io.loginid.core.interfaces.PasskeyManagerAPI
import io.loginid.core.models.LoginIDConfig
import io.loginid.client.api.PasskeysApi
import io.loginid.client.model.PasskeyRenameRequestBody
import io.loginid.client.model.Passkey

/**
 * A service that implements the [PasskeyManagerAPI] for managing passkeys.
 *
 * @param config The LoginID configuration.
 */
class PasskeyManagerService(config: LoginIDConfig) : PasskeyManagerAPI {
    private val managementApi = PasskeysApi(config.getBaseUrl())

    /**
     * Lists all passkeys for the authorized user.
     *
     * @param authorization The authorization token.
     * @return A list of [Passkey] objects.
     */
    override suspend fun listPasskeys(authorization: String?): List<Passkey> {
        return managementApi.passkeysPasskeysList(authorization = authorization)
    }

    /**
     * Renames a passkey.
     *
     * @param id The ID of the passkey to rename.
     * @param name The new name for the passkey.
     * @param authorization The authorization token.
     */
    override suspend fun renamePasskey(id: String, name: String, authorization: String?) {
        managementApi.passkeysPasskeyRename(
            id = id,
            passkeyRenameRequestBody = PasskeyRenameRequestBody(name = name),
            authorization = authorization
        )
    }

    /**
     * Deletes a passkey.
     *
     * @param id The ID of the passkey to delete.
     * @param authorization The authorization token.
     */
    override suspend fun deletePasskey(id: String, authorization: String?) {
        managementApi.passkeysPasskeyDelete(id = id, authorization = authorization)
    }
}
