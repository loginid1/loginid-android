package com.loginid.auth.extensions

import com.loginid.auth.models.FallbackMethodsResult
import io.loginid.client.model.AuthInit

/**
 * Merges cross-authentication and fallback methods into a single list.
 *
 * @return A [FallbackMethodsResult] containing the combined list of methods.
 */
fun AuthInit.mergeFallbackMethods(): FallbackMethodsResult {
    return (this.crossAuthMethods.map { it.value } + this.fallbackMethods.map { it.value })
}
