package com.dermalisys.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dermalisys.BuildConfig
import com.dermalisys.R
import com.dermalisys.databinding.ActivityProfileBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.editprofile.EditProfileActivity
import com.dermalisys.ui.login.LoginActivity
import com.dermalisys.ui.main.MainActivity
import com.dermalisys.ui.preview.PreviewActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val secretToken = BuildConfig.API_SECRET_TOKEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        binding.ivProfile.setColorFilter(resources.getColor(R.color.blue))
        binding.tvProfile.setTextColor(resources.getColor(R.color.blue))

        viewModel.getSession().observe(this) {
            if (it.isLogin) {
                binding.tvEmail.text = it.email
                binding.tvUsername.text = it.name
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            Log.d("detailAccount", " ${it.name}, ${it.email}, ${it.userId}, ${it.isLogin}")
        }

        binding.homeActivity.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right)
            finish()
        }

        binding.cameraButton.setOnClickListener {
            startActivity(Intent(this, PreviewActivity::class.java))
        }

        binding.apply {
            ivEditProfile.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
            }
            tvEditProfile.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
            }
            ivArrowEditProfile.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
            }
        }

        binding.ivArrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed().apply {
                startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right)
                finish()
            }
        }

        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {

                val credentialManager = CredentialManager.create(this@ProfileActivity)
                viewModel.getSession().observe(this@ProfileActivity) { userModel ->
                    val jsonData = "{}"
                    val signature = generateSignature(jsonData, secretToken)
                    viewModel.logout("access_token=${userModel.accessToken}", signature)
                    Log.d("logoutApi", userModel.accessToken)
                }
                auth.signOut()
                credentialManager.clearCredentialState(ClearCredentialStateRequest())

                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                finish()
            }
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
}