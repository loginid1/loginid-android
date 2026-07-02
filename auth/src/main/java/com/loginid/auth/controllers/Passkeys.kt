package com.loginid.auth.controllers

import com.loginid.auth.models.AuthResult
import com.loginid.core.models.LoginIDConfig

class Passkeys(
    private val config: LoginIDConfig
) {
    suspend fun createPasskey(): AuthResult {

        return AuthResult()
    }
}