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
        "https://AIIICE0385888F3SUK9TL3KO.api.dev.loginid.io",
    ))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.createPasskey.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val username = binding.usernameInput.text.toString()
                    val result = lid.createPasskey(this@MainActivity, username)
                    displayResult(result)
                } catch (e: LoginIDError) {
                    displayResult(e)
                }
            }
        }
        binding.authWithPasskey.setOnClickListener {
            // TODO: Implement authenticateWithPasskey
            setResultText("authWithPasskey clicked")
        }
        binding.authWithPasskeyAutofill.setOnClickListener {
            // TODO: Implement authenticateWithPasskeyAutofill
            setResultText("authWithPasskeyAutofill clicked")
        }
        binding.confirmTransaction.setOnClickListener {
            // TODO: Implement confirmTransaction
            setResultText("confirmTransaction clicked")
        }
        binding.listPasskeys.setOnClickListener {
            // TODO: Implement listPasskeys
            setResultText("listPasskeys clicked")
        }
        binding.renamePasskey.setOnClickListener {
            // TODO: Implement renamePasskey
            setResultText("renamePasskey clicked")
        }
        binding.deletePasskey.setOnClickListener {
            // TODO: Implement deletePasskey
            setResultText("deletePasskey clicked")
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

    private fun setResultText(text: String) {
        binding.sdkResult.text = text
    }

    private fun displayResult(result: Any) {
        setResultText(result.toString())
    }
}
