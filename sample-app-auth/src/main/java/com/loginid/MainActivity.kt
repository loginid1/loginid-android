package com.loginid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.databinding.ActivityMainBinding
import com.loginid.auth.LoginIDAuth
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
                lid.createPasskey(this@MainActivity, username)
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
            // TODO: Implement requestOtp
            setResultText("requestOtp clicked")
        }
        binding.validateOtp.setOnClickListener {
            // TODO: Implement validateOtp
            setResultText("validateOtp clicked")
        }
        binding.requestAndSendOtp.setOnClickListener {
            // TODO: Implement requestAndSendOtp
            setResultText("requestAndSendOtp clicked")
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
