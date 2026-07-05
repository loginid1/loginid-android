package com.loginid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.databinding.ActivityMainBinding
import com.loginid.auth.LoginIDAuth
import com.loginid.auth.models.CreatePasskeyOptions
import com.loginid.core.enums.UsernameType
import com.loginid.core.errors.LoginIDError
import com.loginid.core.models.LoginIDConfig
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val lid = LoginIDAuth(config = LoginIDConfig(
        this,
        "https://2D0SU1DM96C9G5JQACPP8RT9.api.loginid.io",
    ))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.createPasskey.setOnClickListener {
            executeLoginID {
                val username = binding.usernameInput.text.toString()
                lid.createPasskey(
                    this@MainActivity,
                    username,
                    CreatePasskeyOptions(usernameType = UsernameType.EMAIL)
                )
            }
        }
        binding.authWithPasskey.setOnClickListener {
            executeLoginID {
                val username = binding.usernameInput.text.toString()
                lid.authenticateWithPasskey(this@MainActivity, username)
            }
        }
        binding.authWithPasskeyAutofill.setOnClickListener {
            executeLoginID {
                lid.authenticateWithPasskeyAutofill(this@MainActivity, binding.usernameInput)
            }
        }
        binding.confirmTransaction.setOnClickListener {
            executeLoginID {
                val username = binding.usernameInput.text.toString()
                lid.confirmTransaction(this@MainActivity, username, "$100")
            }
        }
        binding.listPasskeys.setOnClickListener {
            executeLoginID {
                val passkeys = lid.listPasskeys()
                if (passkeys.isNotEmpty()) {
                    runOnUiThread {
                        binding.passkeyIdInput.setText(passkeys.first().id)
                    }
                }
                passkeys
            }
        }
        binding.renamePasskey.setOnClickListener {
            executeLoginID {
                val passkeyId = binding.passkeyIdInput.text.toString()
                val newName = binding.passkeyNameInput.text.toString()
                lid.renamePasskey(passkeyId, newName)
                "Passkey renamed successfully"
            }
        }
        binding.deletePasskey.setOnClickListener {
            executeLoginID {
                val passkeyId = binding.passkeyIdInput.text.toString()
                lid.deletePasskey(passkeyId)
                "Passkey deleted successfully"
            }
        }
        binding.requestOtp.setOnClickListener {
            executeLoginID {
                lid.requestOtp()
            }
        }
        binding.validateOtp.setOnClickListener {
            executeLoginID {
                val username = binding.usernameInput.text.toString()
                val otp = binding.otpInput.text.toString()
                lid.validateOtp(username, otp)
            }
        }
        binding.requestAndSendOtp.setOnClickListener {
            executeLoginID {
                val username = binding.usernameInput.text.toString()
                lid.requestAndSendOtp(username)
                "OTP request sent"
            }
        }
        binding.getSessionInfo.setOnClickListener {
            // TODO: Implement getSessionInfo
            setResultText("getSessionInfo clicked")
        }
        binding.logout.setOnClickListener {
            // TODO: Implement logout
            setResultText("logout clicked")
        }
    }

    private fun executeLoginID(block: suspend () -> Any) {
        lifecycleScope.launch {
            try {
                val result = block()
                displayResult(result)
            } catch (e: LoginIDError) {
                displayResult(e)
            }
        }
    }

    private fun setResultText(text: String) {
        binding.sdkResult.text = text
    }

    private fun displayResult(result: Any) {
        setResultText(result.toString())
    }
}
