package com.dermalisys.ui.editprofile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dermalisys.BuildConfig
import com.dermalisys.R
import com.dermalisys.data.pref.UserModel
import com.dermalisys.databinding.ActivityEditProfileBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.main.MainActivity
import com.dermalisys.ui.profile.ProfileActivity
import com.dermalisys.util.Result
import kotlinx.coroutines.launch
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
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        resetPassword()
        updateName()

        binding.ivArrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun updateName() {
        lifecycleScope.launch {
            viewModel.getSession().observe(this@EditProfileActivity) { user ->
                binding.btnUpdateName.setOnClickListener {

                    val newName = binding.edName.text.toString()
                    val jsonData = "{\"name\":\"$newName\"}"
                    val signature = generateSignature(jsonData, secretToken)
                    viewModel.updateUserDisplatName(
                        signature,
                        user.userId,
                        newName,
                        user.oneTapLogin
                    ).observe(this@EditProfileActivity) { result ->
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                            }

                            is Result.Success -> {
                                showLoading(false)
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Update Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                                viewModel.saveSession(
                                    UserModel(
                                        user.email,
                                        newName,
                                        user.userId,
                                        ""
                                    )
                                )
                                onBackPressedDispatcher.onBackPressed()
                            }

                            is Result.Error -> {
                                showLoading(false)
                                Log.e("EditProfile", "updateName: ${result.error}")
                            }
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
                        viewModel.resetPassword(signature, user.email)
                            .observe(this@EditProfileActivity) { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        showLoading(true)
                                    }

                                    is Result.Success -> {
                                        showLoading(false)
                                        Toast.makeText(
                                            this@EditProfileActivity,
                                            "Check your email",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onBackPressedDispatcher.onBackPressed()
                                    }

                                    is Result.Error -> {
                                        showLoading(false)
                                        Log.e("EditProfile", "resetPassword: ${result.error}")
                                    }
                                }
                            }
                    }
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    startActivity(Intent(this@EditProfileActivity, ProfileActivity::class.java))
                    finish()
                }
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ProfileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
        finish()
    }
}