# LoginIDMFA

The **LoginIDMFA** Android library provides a powerful and flexible way to orchestrate multi-factor authentication (MFA) flows. It supports various authentication factors and is designed to handle complex authentication scenarios, including a specialized flow for e-commerce checkouts.

## Features

- Stateful MFA session management.
- Support for multiple factors:
    - 🔑 **Passkeys**: Registration, authentication, and transaction confirmation.
    - 📩 **One-Time Passwords (OTP)**: via SMS or Email.
    - 🔗 **External Authenticators**: Integrate with third-party authentication providers.
- **Checkout Flow**: A specialized `LoginIDCheckoutMFA` helper for secure transaction-based authentication.
- **Identity Trust**: Handles wallet and merchant trust tokens for checkout scenarios.

## Requirements

- Android 9.0+ (API level 28+)

## How to Use

### Setup

It's recommended to initialize `LoginIDMFA` or `LoginIDCheckoutMFA` once in your `Application` class.

**For general MFA flows:**
```kotlin
import android.app.Application
import com.loginid.mfa.LoginIDMFA
import com.loginid.core.models.LoginIDConfig

class MyApp : Application() {
    lateinit var loginIdMfa: LoginIDMFA
        private set

    override fun onCreate() {
        super.onCreate()
        val baseUrl = "<YOUR_BASE_URL>"
        val config = LoginIDConfig(this, baseUrl)
        loginIdMfa = LoginIDMFA(config)
    }
}
```

**For checkout flows:**
```kotlin
import android.app.Application
import com.loginid.mfa.LoginIDCheckoutMFA

class MyApp : Application() {
    lateinit var loginIdCheckoutMfa: LoginIDCheckoutMFA
        private set

    override fun onCreate() {
        super.onCreate()
        val baseUrl = "<YOUR_BASE_URL>"
        loginIdCheckoutMfa = LoginIDCheckoutMFA(this, baseUrl)
    }
}
```

### Begin an MFA Flow

Start the MFA process for a user. The result will tell you which factors are available.

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.mfa.LoginIDMFA
import com.loginid.core.errors.LoginIDError
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun beginMfaFlow(loginIdMfa: LoginIDMFA) {
        lifecycleScope.launch {
            try {
                val username = "user@example.com"
                val mfaResult = loginIdMfa.beginFlow(username)

                if (mfaResult.isComplete) {
                    println("Authentication complete! Access token: ${mfaResult.accessToken}")
                } else {
                    println("Next action to perform: ${mfaResult.nextAction}")
                    println("Remaining factors: ${mfaResult.remainingFactors.map { it.type }}")
                }
            } catch (error: LoginIDError) {
                println("Error beginning MFA flow: ${error.message}")
            } catch (e: Exception) {
                println("An unexpected error occurred: ${e.message}")
            }
        }
    }
}
```

### Perform a Passkey Action

After beginning a flow, you can perform one of the available actions.

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.mfa.LoginIDMFA
import com.loginid.mfa.enums.ActionName
import com.loginid.core.errors.LoginIDError
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun performPasskeyAuth(loginIdMfa: LoginIDMFA) {
        lifecycleScope.launch {
            try {
                // The session from beginFlow is stored internally by the SDK
                val mfaResult = loginIdMfa.performAction(ActionName.PASSKEY_AUTH, this@MyActivity)

                if (mfaResult.isComplete) {
                    println("Authentication complete! Access token: ${mfaResult.accessToken}")
                }
            } catch (error: LoginIDError) {
                println("Error with passkey auth: ${error.message}")
            } catch (e: Exception) {
                println("An unexpected error occurred: ${e.message}")
            }
        }
    }
}
```

<details>
<summary>Using the Checkout Flow</summary>

The `LoginIDCheckoutMFA` is specialized for transaction-based authentication.

**1. Begin the checkout flow with a transaction payload:**

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.mfa.LoginIDCheckoutMFA
import com.loginid.core.errors.LoginIDError
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun beginCheckoutFlow(loginIdCheckoutMfa: LoginIDCheckoutMFA) {
        lifecycleScope.launch {
            try {
                val username = "user@example.com"
                val txPayload = """{ "amount": 100, "currency": "USD" }"""
                val mfaResult = loginIdCheckoutMfa.beginFlow(txPayload, username)

                println("Next action for checkout: ${mfaResult.nextAction}")
            } catch (error: LoginIDError) {
                println("Error beginning checkout flow: ${error.message}")
            }
        }
    }
}
```

**2. Perform transaction confirmation:**

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.mfa.LoginIDCheckoutMFA
import com.loginid.mfa.enums.ActionName
import com.loginid.core.errors.LoginIDError
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun confirmTransaction(loginIdCheckoutMfa: LoginIDCheckoutMFA) {
        lifecycleScope.launch {
            try {
                val mfaResult = loginIdCheckoutMfa.performAction(ActionName.PASSKEY_TX, this@MyActivity)

                if (mfaResult.isComplete) {
                    println("Transaction confirmed! Payload signature: ${mfaResult.payloadSignature}")
                }
            } catch (error: LoginIDError) {
                println("Error confirming transaction: ${error.message}")
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
