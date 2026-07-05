package com.loginid.auth.models

/**
 * Represents the result of a configuration verification check.
 *
 * If `verifyConfigSettings()` returns a non-nil value, it indicates a configuration
 * problem that needs to be addressed.
 *
 * @property solution A suggested solution to fix the configuration issue.
 * @property code A machine-readable error code.
 * @property errorMessage A human-readable error message describing the issue.
 */
data class LoginIDConfigResult(
    val solution: String,
    val code: String,
    val errorMessage: String
)
