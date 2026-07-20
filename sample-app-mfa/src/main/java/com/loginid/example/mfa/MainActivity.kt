package com.loginid.example.mfa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.loginid.core.models.LoginIDConfig
import io.loginid.core.stores.SharedPreferencesStorage
import io.loginid.core.utils.KeyStoreManager
import io.loginid.core.utils.TrustID
import com.loginid.example.mfa.databinding.ActivityMainBinding
import io.loginid.mfa.LoginIDCheckoutMFA
import io.loginid.mfa.enums.ActionName
import io.loginid.mfa.models.BeginFlowOptions
import io.loginid.mfa.models.PerformActionOptions
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val baseUrl = "https://AIIICE0385888F3SUK9TL3KO.api.dev.loginid.io"
    private val lid = LoginIDCheckoutMFA(this, baseUrl)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.beginFlow.setOnClickListener {
            executeBlock {
                val username = binding.usernameInput.text.toString()
                val txPayload = binding.txPayloadInput.text.toString()
                val displayName = binding.displayNameInput.text.toString()
                val options = BeginFlowOptions(
                    displayName = displayName,
                )
                lid.beginFlow(txPayload = txPayload, username = username, options = options)
            }
        }
        binding.external.setOnClickListener {
            executeBlock {
                val username = binding.usernameInput.text.toString()
                val token = requestExternalAuthToken(username, baseUrl)
                val options = PerformActionOptions(payload = token)
                lid.performAction(ActionName.EXTERNAL, options)
            }
        }
        binding.passkeyCreate.setOnClickListener {
            executeBlock {
                val displayName = binding.displayNameInput.text.toString()
                val options = PerformActionOptions(
                    activity = this@MainActivity,
                    displayName = displayName,
                )
                lid.performAction(ActionName.PASSKEY_REG, options)
            }
        }
        binding.passkeyAuth.setOnClickListener {
            executeBlock {
                lid.performAction(ActionName.PASSKEY_AUTH, this@MainActivity)
            }
        }
        binding.passkeyAutofill.setOnClickListener {
            executeBlock {
                val options = PerformActionOptions(
                    activity = this@MainActivity,
                    usernameAnchorView = binding.usernameInput
                )
                lid.performAction(ActionName.PASSKEY_AUTH, options)
            }
        }
        binding.transactionConfirmation.setOnClickListener {
            executeBlock {
                lid.performAction(ActionName.PASSKEY_TX, this@MainActivity)
            }
        }
        binding.deleteTrust.setOnClickListener {
            executeBlock {
                val config = LoginIDConfig(this, baseUrl)
                val masterStore = SharedPreferencesStorage(config.getContext())
                val trustId = TrustID(
                    config = config,
                    storage = masterStore,
                    keyStoreManagerFactory = { alias ->
                        KeyStoreManager(alias)
                    }
                )
                trustId.deleteTrustId()
                "Deleted all Trust IDs"
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
