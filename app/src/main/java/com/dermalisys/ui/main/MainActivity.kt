package com.dermalisys.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import com.dermalisys.data.remote.response.login.LoginOkResponse
import com.dermalisys.databinding.ActivityMainBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.profile.ProfileActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        showLoading(false)

        binding.profileActivity.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.cameraButton.setOnClickListener {
            // scan
        }

        setSetup()
    }

    private fun setSetup() {
        with(binding) {
            viewModel.getSession().observe(this@MainActivity) {
                tvUsername.text = it.name
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showLoading(false)
    }

    private fun showLoading(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}