# LoginIDAuth

The **LoginIDAuth** Android library is the main entry point for the LoginID SDK, offering a comprehensive suite of authentication services. It provides all the necessary tools for passkey-based authentication, one-time passwords (OTP), transaction confirmation, and passkey management, serving as a standalone solution for developers.

## Features

- 🔑 **Passkey Authentication & Creation**: Sign in or create passkeys for your users with WebAuthn-compatible authentication.
- 📱 **Transaction Confirmation**: Securely confirm transactions or sensitive actions using passkeys.
- 📩 **One-Time Passwords (OTP)**: Request and validate OTPs as a fallback authentication method.
- 🛠️ **Passkey Management**: Allow users to list, rename, and delete their passkeys.
- ⚙️ **Configuration & Session Utilities**: Verify SDK setup and manage user sessions with ease.

## Requirements

- Android 9.0+ (API level 28+)

## How to Use

### Setup

It's recommended to initialize `LoginIDAuth` once in your `Application` class to have a single instance available throughout your app.

```kotlin
import android.app.Application
import com.loginid.auth.LoginIDAuth

class MyApp : Application() {
    lateinit var loginIdAuth: LoginIDAuth
        private set

    override fun onCreate() {
        super.onCreate()
        val baseUrl = "<YOUR_BASE_URL>"
        loginIdAuth = LoginIDAuth(this, baseUrl)
    }
}
```

You can then access this instance from your Activities or Fragments.

### Create a passkey

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.auth.LoginIDAuth
import com.loginid.core.errors.LoginIDError
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun createPasskey(loginIdAuth: LoginIDAuth) {
        lifecycleScope.launch {
            try {
                val username = "user@example.com"
                val result = loginIdAuth.createPasskey(this@MyActivity, username)
                if (result.isAuthenticated) {
                    println("Passkey created and user authenticated. Token: ${result.token ?: "N/A"}")
                }
            } catch (error: LoginIDError) {
                println("Error creating passkey: ${error.message}")
            } catch (e: Exception) {
                println("An unexpected error occurred: ${e.message}")
            }
        }
    }
}
```

### Authenticate with a passkey

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.auth.LoginIDAuth
import com.loginid.core.errors.LoginIDError
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun authenticateWithPasskey(loginIdAuth: LoginIDAuth) {
        lifecycleScope.launch {
            try {
                val username = "user@example.com"
                val result = loginIdAuth.authenticateWithPasskey(this@MyActivity, username)
                if (result.isAuthenticated) {
                    println("User authenticated with passkey. Token: ${result.token ?: "N/A"}")
                } else if (result.isFallback) {
                    println("Fallback options available: ${result.fallbackOptions}")
                    // Handle fallback (e.g., OTP)
                }
            } catch (error: LoginIDError) {
                println("Error authenticating with passkey: ${error.message}")
            } catch (e: Exception) {
                println("An unexpected error occurred: ${e.message}")
            }
        }
    }
}
```

<details>
<summary>Authenticate with passkey autofill</summary>

```kotlin
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.auth.LoginIDAuth
import com.loginid.core.errors.LoginIDError
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    // assuming usernameInput is an EditText or another View in your layout
    private fun authenticateWithAutofill(loginIdAuth: LoginIDAuth, usernameInput: View) {
        lifecycleScope.launch {
            try {
                val result = loginIdAuth.authenticateWithPasskeyAutofill(this@MyActivity, usernameInput)
                if (result.isAuthenticated) {
                    println("User authenticated with passkey autofill. Token: ${result.token ?: "N/A"}")
                }
            } catch (error: LoginIDError) {
                println("Error authenticating with passkey autofill: ${error.message}")
            } catch (e: Exception) {
                println("An unexpected error occurred: ${e.message}")
            }
        }
    }
}
```
</details>

<details>
<summary>Confirm a transaction</summary>

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.auth.LoginIDAuth
import com.loginid.core.errors.LoginIDError
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun confirmTransaction(loginIdAuth: LoginIDAuth) {
        lifecycleScope.launch {
            try {
                val username = "user@example.com"
                val payload = """{ "amount": 100, "currency": "USD" }"""
                val result = loginIdAuth.confirmTransaction(this@MyActivity, username, payload)
                println("Transaction confirmed. Token: ${result.token}")
            } catch (error: LoginIDError) {
                println("Error confirming transaction: ${error.message}")
            } catch (e: Exception) {
                println("An unexpected error occurred: ${e.message}")
            }
        }
    }
}
```
</details>

<details>
<summary>List passkeys</summary>

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.auth.LoginIDAuth
import com.loginid.core.errors.LoginIDError
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun listPasskeys(loginIdAuth: LoginIDAuth) {
        lifecycleScope.launch {
            try {
                // Assume user is already authenticated and you have an authzToken
                // from a previous createPasskey or authenticateWithPasskey call.
                val authzToken = "user-auth-token" 
                val passkeys = loginIdAuth.listPasskeys(authzToken)
                println("User has ${passkeys.size} passkeys.")
                for (passkey in passkeys) {
                    println("- Passkey ID: ${passkey.id}")
                }
            } catch (error: LoginIDError) {
                println("Error listing passkeys: ${error.message}")
            } catch (e: Exception) {
                println("An unexpected error occurred: ${e.message}")
            }
        }
    }
}
```
</details>

## Contact and Support

- **Email**: support@loginid.io
- **Documentation**: https://docs.loginid.io
- **Forum**: https://forum.loginid.dev
- **Bug Reports**: https://loginid.dev
- **Dashboard**: https://dashboard.loginid.io

## License

This project is licensed under the Apache 2.0 License. See LICENSE.md for details.
