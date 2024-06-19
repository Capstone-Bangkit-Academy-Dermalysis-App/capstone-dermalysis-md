package com.dermalisys.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.dermalisys.BuildConfig
import com.dermalisys.R
import com.dermalisys.databinding.ActivityRegisterBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.login.LoginActivity
import com.dermalisys.ui.main.MainActivity
import com.dermalisys.util.Result
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.googleLogin.setOnClickListener {
            showLoading(true)
            signIn()
        }

        binding.guest.setOnClickListener {
            showLoading(true)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.tvToLogin.setOnClickListener {
            showLoading(true)
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right)
            finish()
        }

        binding.btnRegister.setOnClickListener {
            try {
                register()
            } catch (e: HttpException) {
                Log.e("FailedRegister", e.message.toString())
            }
        }
    }

    private fun register() {
        with(binding) {
            val name = edName.text.toString()
            val email = edEmail.text.toString()
            val password = edPassword.text.toString()
            viewModel.register(email, password, name).observe(this@RegisterActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        setupAction(result.data.message)
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        setupFail(result.error)
                        Log.e("FailedRegister", result.error)
                    }
                }
            }
        }
    }

    private fun setupAction(message: String) {
        showLoading(true)
        AlertDialog.Builder(this).apply {
            setTitle("Success!")
            setMessage(message)
            setPositiveButton("Continue") { _, _ ->
                startActivity(Intent(context, LoginActivity::class.java))
                finish()
            }
            create()
            show()
        }
    }

    private fun setupFail(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Failed")
            setMessage(message)
            setPositiveButton("Continue") { _, _ ->
            }
            create()
            show()
        }
    }

    private fun signIn() {

        showLoading(false)

        val credentialManager = CredentialManager.create(this) //import from androidx.CredentialManager

        val googleIdOption = GetSignInWithGoogleOption.Builder(BuildConfig.WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder() //import from androidx.CredentialManager
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential( //import from androidx.CredentialManager
                    request = request,
                    context = this@RegisterActivity,
                )
                handleSignIn(result)
                showLoading(true)
            } catch (e: GetCredentialException) { //import from androidx.CredentialManager
                Log.d("Error2", e.message.toString())
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    // Process Login dengan Firebase Auth
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }
            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun showLoading(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "LoginActivity"
    }



}