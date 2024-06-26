package com.dermalisys.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.dermalisys.BuildConfig
import com.dermalisys.R
import com.dermalisys.data.pref.UserModel
import com.dermalisys.data.remote.response.login.LoginOkResponse
import com.dermalisys.data.remote.response.storenewuser.StoreNewUserResponse
import com.dermalisys.databinding.ActivityLoginBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.custom.PasswordEditText
import com.dermalisys.ui.main.MainActivity
import com.dermalisys.ui.register.RegisterActivity
import com.dermalisys.ui.resetpassword.ResetPasswordActivity
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
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var passCustomET: PasswordEditText

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val secretToken = BuildConfig.API_SECRET_TOKEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        passCustomET = binding.edPassword

        val helperError = binding.password

        passCustomET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 7) {
                    binding.btnLogin.isEnabled = true
                    helperError.isHelperTextEnabled = false
                    helperError.isErrorEnabled = false
                    helperError.isPasswordVisibilityToggleEnabled = true
                } else {
                    binding.btnLogin.isEnabled = false
                    helperError.isErrorEnabled = true
                    helperError.isPasswordVisibilityToggleEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.googleLogin.setOnClickListener {
            showLoading(true)
            oneTapLogin()
        }

        try {
            login()
        } catch (e: HttpException) {
            Log.e("FailedRegister", e.message.toString())
        }

        binding.guest.setOnClickListener {
            showLoading(true)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.tvToRegister.setOnClickListener {
            showLoading(true)
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left)
            finish()
        }

        resetPassword()
    }

    private fun login() {
        with(binding) {
            btnLogin.setOnClickListener {
                val email = edEmail.text.toString()
                val password = edPassword.text.toString()
                viewModel.login(email, password).observe(this@LoginActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            setupAction(result.data.message, result.data)
                        }

                        is Result.Error -> {
                            showLoading(false)
                            Log.e("loginError", result.error)
                            setupFail(result.error)
                        }
                    }
                }
            }
        }
    }

    private fun setupAction(message: String, result: LoginOkResponse) {

        val email = result.data.firstOrNull()!!.user.email
        val displayName = result.data.firstOrNull()!!.user.displayName
        val userId = result.data.firstOrNull()!!.user.uid
        val accessToken = result.data.firstOrNull()!!.user.stsTokenManager.accessToken

        viewModel.saveSession(UserModel(email, displayName, userId, accessToken))

        AlertDialog.Builder(this).apply {
            setTitle("Success!")
            setMessage(message)
            setPositiveButton("Continue") { _, _ ->
                startActivity(Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                })
                finish()
            }
            create()
            show()
        }
    }

    private fun setupActionTwo(result: StoreNewUserResponse) {
        val email = result.data.email
        val displayName = result.data.name
        val userId = result.data.id
        val accessToken = "OneTapLogin"

        viewModel.saveSession(UserModel(email, displayName, userId, accessToken))
        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        })
        finish()
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

    private fun generateSignature(data: String, secretToken: String): String {
        val algorithm = "HmacSHA256"
        val mac = Mac.getInstance(algorithm)
        val keySpec = SecretKeySpec(secretToken.toByteArray(), algorithm)
        mac.init(keySpec)
        val hash = mac.doFinal(data.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun oneTapLogin() {
        val credentialManager =
            CredentialManager.create(this) //import from androidx.CredentialManager

        val googleIdOption = GetSignInWithGoogleOption.Builder(BuildConfig.WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder() //import from androidx.CredentialManager
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    //import from androidx.CredentialManager
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) { //import from androidx.CredentialManager
                Log.d("Error", e.message.toString())
            }
        }
        showLoading(false)
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                Log.d("credentialCheck", credential.toString())
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    // Process Login dengan Firebase Auth
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
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
                    user?.let {
                        val uid = it.uid
                        val displayName = it.displayName ?: ""
                        val email = it.email ?: ""
                        val jsonData =
                            "{\"id\":\"$uid\",\"name\":\"$displayName\",\"email\":\"$email\"}"
                        Log.d("User Info", jsonData)
                        val signature = generateSignature(jsonData, secretToken)
                        viewModel.storeNewUser(signature, uid, displayName, email)
                            .observe(this@LoginActivity) { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        showLoading(true)
                                    }

                                    is Result.Success -> {
                                        showLoading(false)
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        setupActionTwo(result.data)
                                    }

                                    is Result.Error -> {
                                        showLoading(false)
                                        Log.e("loginOneTapError", "Result Error" + result.error)
                                    }
                                }
                            }
                        Log.d("User Info", "UID: $uid, Display Name: $displayName, Email: $email")
                    }

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun resetPassword() {
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }

    private fun showLoading(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}