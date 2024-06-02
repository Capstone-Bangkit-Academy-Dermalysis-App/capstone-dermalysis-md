package com.dermalisys.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.dermalisys.R
import com.dermalisys.databinding.ActivityLoginBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding.googleLogin.setOnClickListener {

        }
    }

    private fun signIn() {
        val credentialManager = CredentialManager.create(this) //import from androidx.CredentialManager
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id))
            .build()
        val request = GetCredentialRequest.Builder() //import from androidx.CredentialManager
            .addCredentialOption(googleIdOption)
            .build()
    }
}