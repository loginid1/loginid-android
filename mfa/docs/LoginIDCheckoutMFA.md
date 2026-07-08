# LoginIDCheckoutMFA

The **LoginIDCheckoutMFA** Android library provides a specialized authentication flow for apps that require both multi-factor authentication (MFA) and identity trust during checkout scenarios. It is built on top of [**LoginIDMFA**](../README.md) and optimized for passkey-based transaction confirmation.

## Features

- 🔑 **Passkey Authentication & Creation** – Sign in or create passkeys for your users with WebAuthn-compatible authentication.
- 🛒 **Checkout-Specific MFA Flows** – Bind MFA sessions to transaction payloads for purchase confirmations.
- 🔄 **Flexible Action Execution** – Perform actions such as passkey authentication, registration, transaction confirmation, or external auth.

## Requirements

- Android 9.0+ (API level 28+)

## How to Use

### Setup

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

### On Component Mount

Call `beginFlow` when your screen loads to start an MFA session. The response tells you whether the user can confirm the transaction directly with a passkey, or if they need to sign in first.

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.core.errors.LoginIDError
import com.loginid.mfa.LoginIDCheckoutMFA
import com.loginid.mfa.enums.ActionName
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun startMFAFlow(loginIdCheckoutMfa: LoginIDCheckoutMFA) {
        lifecycleScope.launch {
            try {
                // 1. Start MFA session for this checkout
                val txPayload = """{ "amount": 100 }"""
                val session = loginIdCheckoutMfa.beginFlow(txPayload = txPayload)

                // 2. Decide what UI to show next
                when (session.nextAction) {
                    ActionName.PASSKEY_TX -> {
                        // User can confirm the transaction with a passkey → show checkout UI
                    }
                    ActionName.PASSKEY_AUTH -> {
                        // User needs to log in with a passkey or a third-party (e.g. bank login) → show login UI
                    }
                    else -> {
                        // Handle other cases
                    }
                }
            } catch (error: LoginIDError) {
                println("Error starting MFA flow: ${error.message}")
            }
        }
    }
}
```

<details>
<summary>Add passkey after bank or third-party login</summary>

Once your backend has verified a user’s login with a bank or third party, return a LoginID authorization token.

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.core.errors.LoginIDError
import com.loginid.mfa.LoginIDCheckoutMFA
import com.loginid.mfa.enums.ActionName
import com.loginid.mfa.models.PerformActionOptions
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun handleBankLogin(loginIdCheckoutMfa: LoginIDCheckoutMFA) {
        lifecycleScope.launch {
            try {
                // 1. Verify login with your backend
                val authToken = MyBackend.verifyBankLogin() // This is a placeholder for your backend call

                // 2. Confirm external login with the token
                val options = PerformActionOptions(payload = authToken)
                val updated = loginIdCheckoutMfa.performAction(ActionName.EXTERNAL, options)

                // 3. If prompted, register a passkey
                if (updated.nextAction == ActionName.PASSKEY_REG) {
                    loginIdCheckoutMfa.performAction(ActionName.PASSKEY_REG, this@MyActivity)
                }
            } catch (error: LoginIDError) {
                println("Error with MFA flow: ${error.message}")
            }
        }
    }
}
```
</details>

<details>
<summary>Wallet signin with a passkey</summary>

On the login screen, if `beginFlow` indicates `.PASSKEY_AUTH`, let the user sign in with their passkey:

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.core.errors.LoginIDError
import com.loginid.mfa.LoginIDCheckoutMFA
import com.loginid.mfa.enums.ActionName
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun walletSignIn(loginIdCheckoutMfa: LoginIDCheckoutMFA) {
        lifecycleScope.launch {
            try {
                // beginFlow returned .PASSKEY_AUTH
                val result = loginIdCheckoutMfa.performAction(ActionName.PASSKEY_AUTH, this@MyActivity)
                if (result.isComplete) {
                    println("Wallet user signed in")
                }
            } catch (error: LoginIDError) {
                println("Error signing in with passkey: ${error.message}")
            }
        }
    }
}
```
</details>

<details>
<summary>Wallet signin with passkey autofill</summary>

On the login screen, you can trigger passkey autofill as soon as the view loads and wait for the user to complete it:

```kotlin
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.core.errors.LoginIDError
import com.loginid.mfa.LoginIDCheckoutMFA
import com.loginid.mfa.enums.ActionName
import com.loginid.mfa.models.PerformActionOptions
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    // assuming usernameInput is an EditText or another View in your layout
    private fun autoFillWalletSignIn(loginIdCheckoutMfa: LoginIDCheckoutMFA, usernameInput: View) {
        lifecycleScope.launch {
            try {
                // beginFlow returned .PASSKEY_AUTH
                val options = PerformActionOptions(usernameAnchorView = usernameInput)
                val signedInSession = loginIdCheckoutMfa.performAction(ActionName.PASSKEY_AUTH, this@MyActivity, options)
                if (signedInSession.isComplete) {
                    println("Wallet user signed in with autofill")
                }
            } catch (error: LoginIDError) {
                println("Error signing in with passkey autofill: ${error.message}")
            }
        }
    }
}
```
</details>

<details>
<summary>Confirm payment with a passkey</summary>

If `beginFlow` returned `.PASSKEY_TX`, show the checkout page and let the user confirm the payment directly with their passkey. This acts as both a sign-in and payment confirmation in one step:

```kotlin
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.core.errors.LoginIDError
import com.loginid.mfa.LoginIDCheckoutMFA
import com.loginid.mfa.enums.ActionName
import kotlinx.coroutines.launch

class MyActivity : AppCompatActivity() {
    private fun confirmPayment(loginIdCheckoutMfa: LoginIDCheckoutMFA) {
        lifecycleScope.launch {
            try {
                // beginFlow returned .PASSKEY_TX
                val signedInSession = loginIdCheckoutMfa.performAction(ActionName.PASSKEY_TX, this@MyActivity)
                if (signedInSession.isComplete) {
                    println("Wallet user confirmed payment. Signature: ${signedInSession.payloadSignature}")
                }
            } catch (error: LoginIDError) {
                println("Error confirming payment: ${error.message}")
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
