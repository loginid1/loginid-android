package com.loginid.auth.controllers

import com.loginid.auth.models.DeletePasskeyOptions
import com.loginid.auth.models.ListPasskeysOptions
import com.loginid.auth.models.PasskeyDetails
import com.loginid.auth.models.RenamePasskeyOptions
import com.loginid.core.interfaces.PasskeyManagerAPI
import com.loginid.core.stores.SessionManager

internal class PasskeyManager(
    private val session: SessionManager,
    private val passkeyManagerApi: PasskeyManagerAPI,
) {
    suspend fun listPasskeys(options: ListPasskeysOptions?): List<PasskeyDetails> {
        val authzToken = session.getAuthzToken(options?.authzToken)
        val passkeys = passkeyManagerApi.listPasskeys(authorization = authzToken)
        return PasskeyDetails.fromArray(passkeys)
    }

    suspend fun renamePasskey(id: String, name: String, options: RenamePasskeyOptions?) {
        val authzToken = session.getAuthzToken(options?.authzToken)
        passkeyManagerApi.renamePasskey(
            id = id,
            name = name,
            authorization = authzToken
        )
    }

    suspend fun deletePasskey(id: String, options: DeletePasskeyOptions?) {
        val authzToken = session.getAuthzToken(options?.authzToken)
        passkeyManagerApi.deletePasskey(
            id = id,
            authorization = authzToken
        )
    }
}
