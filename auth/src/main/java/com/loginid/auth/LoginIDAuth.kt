package com.loginid.auth

import com.loginid.auth.models.AuthResult
import com.loginid.core.models.LoginIDConfig
import com.loginid.core.utils.TaskHandler

class LoginIDAuth(config: LoginIDConfig) {
    suspend fun createPasskey(): AuthResult {
        return TaskHandler.executeTask {
            AuthResult()
        }
    }
}