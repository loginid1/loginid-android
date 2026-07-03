package com.loginid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.loginid.databinding.ActivityMainBinding
// import com.loginid.auth.LoginIDAuth
// import com.loginid.core.models.LoginIDConfig

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // private val lid = LoginIDAuth(config = LoginIDConfig(this, "YOUR_BASE_URL"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.createPasskey.setOnClickListener {
            // TODO: Implement createPasskey
            setResultText("createPasskey clicked")
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
}
