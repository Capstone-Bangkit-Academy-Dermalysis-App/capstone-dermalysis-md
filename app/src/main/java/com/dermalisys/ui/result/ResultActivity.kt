package com.dermalisys.ui.result

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.dermalisys.databinding.ActivityResultBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.login.LoginActivity
import com.dermalisys.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    private val viewModel: ResultViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        viewModel.getSesssion().observe(this) {
            if (!it.isLogin) {
                binding.fabSave.visibility = View.VISIBLE
                binding.fabSave.setOnClickListener {
                    setupFail()
                }
            }
        }

        val imageUriString = intent.getStringExtra("image")
        val imageUri = Uri.parse(imageUriString)

        val createdAt = intent.getStringExtra("date")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(createdAt)
        val onlyDate = outputFormat.format(date!!)

        binding.imageView.setImageURI(imageUri)
        binding.tvDiseaseName.text = intent.getStringExtra("name")
        binding.tvLatinName.text = intent.getStringExtra("latinName")
        binding.tvDescription.text = intent.getStringExtra("description")
        binding.tvConfidenceScore.text = intent.getStringExtra("confidenceScore")

        binding.tvCaused1.text = intent.getStringExtra("cause1")
        binding.tvCaused2.text = intent.getStringExtra("cause2")

        binding.tvSymptoms1.text = intent.getStringExtra("symptom1")
        binding.tvSymptoms2.text = intent.getStringExtra("symptom2")

        binding.tvTreatment.text = intent.getStringExtra("treatment")

        binding.tvDate.text = onlyDate

        binding.ivArrowBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupFail() {
        AlertDialog.Builder(this).apply {
            setTitle("Must Login!")
            setMessage("Continue to login?")
            setPositiveButton("Login") { _, _ ->
                val intent = Intent(this@ResultActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(intent)
                finish()
            }
            setNegativeButton("Cancel") { _, _ -> }
            create()
            show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ResultActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
        finish()
    }
}