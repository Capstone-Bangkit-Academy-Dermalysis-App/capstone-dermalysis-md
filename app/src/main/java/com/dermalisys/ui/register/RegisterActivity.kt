package com.dermalisys.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dermalisys.R
import com.dermalisys.databinding.ActivityRegisterBinding
import com.dermalisys.ui.login.LoginActivity
import com.dermalisys.ui.main.MainActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding.guest.setOnClickListener {
            showLoading(true)
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.tvToLogin.setOnClickListener {
            showLoading(true)
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun showLoading(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}