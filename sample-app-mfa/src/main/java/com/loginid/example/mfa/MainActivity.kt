package com.loginid.example.mfa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.loginid.example.mfa.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.beginFlow.setOnClickListener {
            executeBlock {
                // TODO: Implement MFA SDK call
                "Begin Flow clicked"
            }
        }
        binding.external.setOnClickListener {
            executeBlock {
                val username = binding.usernameInput.text.toString()
                // TODO: Implement MFA SDK call
                "External clicked for $username"
            }
        }
        binding.passkeyCreate.setOnClickListener {
            executeBlock {
                val displayName = binding.displayNameInput.text.toString()
                // TODO: Implement MFA SDK call
                "Passkey Create clicked with display name: $displayName"
            }
        }
        binding.passkeyAuth.setOnClickListener {
            executeBlock {
                // TODO: Implement MFA SDK call
                "Passkey Authenticate clicked"
            }
        }
        binding.passkeyAutofill.setOnClickListener {
            executeBlock {
                // TODO: Implement MFA SDK call
                "Passkey Autofill clicked"
            }
        }
        binding.transactionConfirmation.setOnClickListener {
            executeBlock {
                val txPayload = binding.txPayloadInput.text.toString()
                // TODO: Implement MFA SDK call
                "Transaction Confirmation clicked with payload: $txPayload"
            }
        }
        binding.deleteTrust.setOnClickListener {
            executeBlock {
                // TODO: Implement MFA SDK call
                "Delete Trust clicked"
            }
        }
    }

    private fun executeBlock(block: suspend () -> Any) {
        lifecycleScope.launch {
            try {
                val result = block()
                displayResult(result)
            } catch (e: Exception) {
                displayResult(e)
            }
        }
    }

    private fun setResultText(text: String) {
        binding.sessionResult.text = text
    }

    private fun displayResult(result: Any) {
        setResultText(result.toString())
    }
}
