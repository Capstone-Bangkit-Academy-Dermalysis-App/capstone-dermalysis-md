package com.dermalisys.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        auth = Firebase.auth

        binding.ivProfile.setColorFilter(resources.getColor(R.color.blue))
        binding.tvProfile.setTextColor(resources.getColor(R.color.blue))

        viewModel.getSession().observe(this) {
            if (it.isLogin) {
                binding.tvEmail.text = it.email
                binding.tvUsername.text = it.name
            }
        }

        binding.homeActivity.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
            finish()
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right)
        }

        binding.cameraButton.setOnClickListener {
            startActivity(Intent(this, PreviewActivity::class.java))
        }

        viewModel.getSession().observe(this) {
            if (it.oneTapLogin != "OneTapLogin") {
                binding.apply {
                    ivEditProfile.setOnClickListener {
                        startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
                        finish()
                    }
                    tvEditProfile.setOnClickListener {
                        startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
                        finish()
                    }
                    ivArrowEditProfile.setOnClickListener {
                        startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
                        finish()
                    }
                }
            } else {
                binding.apply {
                    ivEditProfile.visibility = View.GONE
                    tvEditProfile.visibility = View.GONE
                    ivArrowEditProfile.visibility = View.GONE
                }
            }
        }

        binding.ivArrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right)
        }

        binding.btnLogout.setOnClickListener {

            lifecycleScope.launch {

                val credentialManager = CredentialManager.create(this@ProfileActivity)
                auth.signOut()
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            }
            viewModel.logout()
            Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
        finish()
        overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right)
    }
}