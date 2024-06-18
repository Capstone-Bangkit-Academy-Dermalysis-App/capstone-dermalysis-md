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
import com.dermalisys.R
import com.dermalisys.databinding.ActivityProfileBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.login.LoginActivity
import com.dermalisys.ui.main.MainActivity
import com.dermalisys.ui.preview.PreviewActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

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
        val firebaseUser = auth.currentUser

        viewModel.getSession().observe(this) {
            if (!it.isLogin) {
                // Not signed in, launch the Login activity
                startActivity(Intent(this, LoginActivity::class.java))
                Toast.makeText(this, "You need to login", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                binding.tvEmail.text = it.email
                binding.tvUsername.text = it.name
            }
        }

        binding.homeActivity.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.cameraButton.setOnClickListener {
            startActivity(Intent(this, PreviewActivity::class.java))
        }

        binding.ivArrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {

                val credentialManager = CredentialManager.create(this@ProfileActivity)
                viewModel.getSession().observe(this@ProfileActivity) { userModel ->
                    viewModel.logout("access_token=${userModel.accessToken}", userModel.token)
                    Log.d("logoutApi", userModel.accessToken)
                }
                auth.signOut()
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            }
        }
    }
}