package com.dermalisys.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dermalisys.BuildConfig
import com.dermalisys.databinding.ActivityMainBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.adapter.HistoryAdapter
import com.dermalisys.ui.preview.PreviewActivity
import com.dermalisys.ui.profile.ProfileActivity
import kotlinx.coroutines.launch
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

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
            startActivity(Intent(this, PreviewActivity::class.java))
        }

        setSetup()

        binding.rvHistory.layoutManager = LinearLayoutManager(this)

        val signature = generateSignature("{}", SECRET_TOKEN)

        viewModel.getSession().observe(this@MainActivity) {
            lifecycleScope.launch {
                getHistory(signature, it.userId, "access_token=${it.accessToken}")
            }
        }
    }

    private fun setSetup() {
        with(binding) {
            viewModel.getSession().observe(this@MainActivity) {
                tvUsername.text = it.name
            }
        }
    }

    private suspend fun getHistory(signature: String, userId: String, accessToken: String) {
        val adapter = HistoryAdapter()
        try {
            binding.rvHistory.adapter = adapter
            viewModel.getHistory(signature, userId, accessToken).observe(this) {
                adapter.submitData(lifecycle, it)
            }
        } catch (e: Exception) {
            Log.e("adapterError", e.message.toString())
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

    override fun onResume() {
        super.onResume()
        showLoading(false)
    }

    private fun showLoading(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    companion object {
        private const val SECRET_TOKEN = BuildConfig.API_SECRET_TOKEN
    }
}