package com.loginid.example.mfa

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.text.Charsets.UTF_8

// This is a management API key. It should not be stored in the app.
// This is for demonstration purposes only.
// In a real application, this should be handled by a backend server.
private const val LOGINID_API_KEY = ""

suspend fun requestExternalAuthToken(username: String, baseUrl: String): String {
    return withContext(Dispatchers.IO) {
        // Create the Basic token
        val basicToken = Base64.encodeToString(
            "$LOGINID_API_KEY:".toByteArray(UTF_8),
            Base64.NO_WRAP
        )

        // Build the request URL
        val url = URL("$baseUrl/fido2/v2/mgmt/grant/external-auth")
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Basic $basicToken")
        connection.doOutput = true

        // Body
        val body = JSONObject().apply {
            put("username", username)
        }.toString()

        OutputStreamWriter(connection.outputStream).use {
            it.write(body)
        }

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)
            json.getString("token")
        } else {
            val errorStream = connection.errorStream ?: connection.inputStream
            val errorResponse = errorStream.bufferedReader().use { it.readText() }
            val message = try {
                val json = JSONObject(errorResponse)
                json.optString("message", json.optString("msg", "Unknown error"))
            } catch (e: Exception) {
                errorResponse
            }
            throw Exception("Error: $responseCode - $message")
        }
    }
}
