package com.loginid.core.models

import android.content.Context

/**
 * A configuration object that stores and manages the LoginID service configurable options.
 *
 * @param context The Android application context.
 * @param baseUrl The base URL of the LoginID service. This value is used to resolve the App ID and make API calls.
 * @param appId The application ID. If not provided, it will be extracted from the baseUrl.
 * @param useTrustId A flag to enable or disable the use of Trust ID. Defaults to false.
 */
class LoginIDConfig(context: Context, baseUrl: String, appId: String = "", useTrustId: Boolean = false) {
    /**
     * The Android application context.
     */
    private val context: Context = context
    /**
     * The base URL of the LoginID service.
     */
    private val baseUrl: String = baseUrl
    /**
     * The application ID.
     */
    private val appId: String = appId
    /**
     * A flag to enable or disable the use of Trust ID.
     */
    private val useTrustId: Boolean = useTrustId

    init {
        if (baseUrl.isEmpty()) {
            throw IllegalArgumentException("Base URL is missing")
        }
    }

    /**
     * Returns the configured Android application context.
     *
     * @return The application context.
     */
    fun getContext(): Context {
        return context
    }

    /**
     * Returns the configured base URL.
     *
     * @return A string representing the base URL.
     */
    fun getBaseUrl(): String {
        return baseUrl
    }

    /**
     * Extracts the App ID from the base URL using a regular expression.
     * If an App ID was provided during initialization, it will be returned instead.
     *
     * @throws IllegalArgumentException if the base URL does not contain a valid App ID and no App ID was provided.
     * @return A string representing the App ID.
     */
    fun getAppId(): String {
        if (appId.isNotEmpty()) {
            return appId
        }

        // Regex to capture the subdomain part before the first period in the baseUrl
        val pattern = Regex("https?://([^.]+)\\.")
        val matchResult = pattern.find(baseUrl)
        return matchResult?.groupValues?.get(1) ?:
        throw IllegalArgumentException("Invalid base URL. App ID not found.")
    }

    /**
     * Returns whether Trust ID is enabled.
     *
     * @return A boolean indicating if Trust ID is enabled.
     */
    fun useTrustId(): Boolean {
        return useTrustId
    }
}
