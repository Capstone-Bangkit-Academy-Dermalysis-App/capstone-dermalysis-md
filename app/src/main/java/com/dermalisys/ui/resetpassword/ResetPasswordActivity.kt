package com.dermalisys.ui.resetpassword

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dermalisys.BuildConfig
import com.dermalisys.R
import com.dermalisys.databinding.ActivityResetPasswordBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.login.LoginActivity
import com.dermalisys.ui.main.MainActivity
import com.dermalisys.util.Result
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    private val viewModel: ResetPasswordViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val secretToken = BuildConfig.API_SECRET_TOKEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnReset.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val jsonData = "{\"email\":\"$email\"}"
            val signature = generateSignature(jsonData, secretToken)
            viewModel.resetPassword(signature, email).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle("Success!")
                            setMessage(result.data.message)
                            setPositiveButton("Continue") { _, _ ->
                                startActivity(Intent(context, LoginActivity::class.java))
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                    is Result.Error -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle("Error!")
                            setMessage(result.error)
                            setPositiveButton("Continue") { _, _ ->
                            }
                            create()
                            show()
                        }
                    }
                }
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

    private fun showLoading(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}