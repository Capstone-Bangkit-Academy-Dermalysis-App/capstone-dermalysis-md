package com.dermalisys.ui.editprofile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dermalisys.BuildConfig
import com.dermalisys.R
import com.dermalisys.databinding.ActivityEditProfileBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.login.LoginActivity
import com.dermalisys.ui.profile.ProfileActivity
import com.dermalisys.util.Result
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    private val viewModel: EditProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val secretToken = BuildConfig.API_SECRET_TOKEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        resetPassword()
    }

    private fun updateName() {
        binding.btnUpdateName.setOnClickListener {

            viewModel.getSession().observe(this) { user ->
                val newName = binding.edName.text.toString()
                val jsonData = "{\"email\":\"$newName\"}"
                val signature = generateSignature(jsonData, secretToken)
                viewModel.updateUserDisplatName(signature, user.userId, newName, user.accessToken).observe(this) { result ->
                    when(result) {
                        is Result.Loading -> {

                        }
                        is Result.Success -> {

                        }
                        is Result.Error -> {

                        }
                    }
                }
            }
        }
    }

    private fun resetPassword() {

        binding.btnReset.setOnClickListener {

            AlertDialog.Builder(this).apply {
                setTitle("Reset Password?")
                setMessage("Continue to reset your password?")
                setPositiveButton("Continue") { _, _ ->

                    viewModel.getSession().observe(this@EditProfileActivity) { user ->

                        val jsonData = "{\"email\":\"${user.email}\"}"
                        val signature = generateSignature(jsonData, secretToken)
                        viewModel.resetPassword(signature, user.email).observe(this@EditProfileActivity) { result ->
                            when(result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                }
                                is Result.Success -> {
                                    showLoading(false)
                                    Toast.makeText(this@EditProfileActivity, "Reset password berhasil", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@EditProfileActivity, ProfileActivity::class.java))
                                    finish()
                                }
                                is Result.Error -> {
                                    showLoading(false)
                                    Log.e("EditProfile", "resetPassword: ${result.error}")
                                }
                            }
                        }
                    }
                }
                setNegativeButton("Cancel") { dialog, _ -> }
                create()
                show()
            }
        }
    }

    private fun showLoading(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
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