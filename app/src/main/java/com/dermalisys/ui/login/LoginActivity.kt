package com.dermalisys.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.dermalisys.BuildConfig
import com.dermalisys.R
import com.dermalisys.data.pref.UserModel
import com.dermalisys.data.remote.response.login.LoginOkResponse
import com.dermalisys.databinding.ActivityLoginBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.main.MainActivity
import com.dermalisys.ui.register.RegisterActivity
import com.dermalisys.util.Result
import retrofit2.HttpException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
//    private lateinit var auth: FirebaseAuth

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

        // Initialize Firebase Auth
//        auth = Firebase.auth

        binding.googleLogin.setOnClickListener {
            showLoading(true)
//            signIn()
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
    }

    private fun login() {
        with(binding) {
            btnLogin.setOnClickListener {
                Log.d("loginbtn", "tombol ditekan")
                showLoading(true)
                val email = edEmail.text.toString()
                val password = edPassword.text.toString()
                viewModel.login(email, password).observe(this@LoginActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            val jsonData = "{\"email\":\"$email\",\"password\":\"$password\"}"
                            val signature = generateSignature(jsonData, secretToken)
                            setupAction(signature, result.data.message, result.data, )
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

    private fun setupAction(token: String, message: String, result: LoginOkResponse) {
        val email = binding.edEmail.text.toString()

        val displayName = result.data.firstOrNull()!!.user.displayName
        val userId = result.data.firstOrNull()!!.user.uid
        val accessToken = result.data.firstOrNull()!!.user.stsTokenManager.accessToken
        Log.d("LoginUserModel", email)
        Log.d("LoginUserModel", token)
        Log.d("LoginUserModel", displayName)
        Log.d("LoginUserModel", userId)
        Log.d("LoginUserModel", accessToken)
        viewModel.saveSession(UserModel(email, token, displayName, userId, accessToken))

        Log.d("displayName", displayName)

        AlertDialog.Builder(this).apply {
            setTitle("Success!")
            setMessage(message)
            setPositiveButton("Continue") { _, _ ->
                startActivity(Intent(context, MainActivity::class.java))
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

    private fun generateSignature(data: String, secretToken: String): String {
        val algorithm = "HmacSHA256"
        val mac = Mac.getInstance(algorithm)
        val keySpec = SecretKeySpec(secretToken.toByteArray(), algorithm)
        mac.init(keySpec)
        val hash = mac.doFinal(data.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(R.layout.activity_main)
    }



//    private fun signIn() {
//        val credentialManager = CredentialManager.create(this) //import from androidx.CredentialManager
//
//        val googleIdOption = GetSignInWithGoogleOption.Builder(BuildConfig.WEB_CLIENT_ID)
//            .build()
//
//        val request = GetCredentialRequest.Builder() //import from androidx.CredentialManager
//            .addCredentialOption(googleIdOption)
//            .build()
//
//        lifecycleScope.launch {
//            try {
//                val result: GetCredentialResponse = credentialManager.getCredential( //import from androidx.CredentialManager
//                    request = request,
//                    context = this@LoginActivity,
//                )
//                handleSignIn(result)
//            } catch (e: GetCredentialException) { //import from androidx.CredentialManager
//                Log.d("Error", e.message.toString())
//            }
//        }
//        showLoading(false)
//    }
//
//    private fun handleSignIn(result: GetCredentialResponse) {
//        // Handle the successfully returned credential.
//        when (val credential = result.credential) {
//            is CustomCredential -> {
//                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
//                    // Process Login dengan Firebase Auth
//                    try {
//                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
//                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
//                    } catch (e: GoogleIdTokenParsingException) {
//                        Log.e(TAG, "Received an invalid google id token response", e)
//                    }
//                } else {
//                    // Catch any unrecognized custom credential type here.
//                    Log.e(TAG, "Unexpected type of credential")
//                }
//            }
//            else -> {
//                // Catch any unrecognized credential type here.
//                Log.e(TAG, "Unexpected type of credential")
//            }
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "signInWithCredential:success")
//                    val user: FirebaseUser? = auth.currentUser
//                    updateUI(user)
//                } else {
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    updateUI(null)
//                }
//            }
//    }
//
//    private fun updateUI(currentUser: FirebaseUser?) {
//        if (currentUser != null) {
//            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//            finish()
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
//    }
//
//    companion object {
//        private const val TAG = "LoginActivity"
//    }

    private fun showLoading(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}