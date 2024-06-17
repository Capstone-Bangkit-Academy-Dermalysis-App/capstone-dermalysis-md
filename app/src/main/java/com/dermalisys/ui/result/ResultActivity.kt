package com.dermalisys.ui.result

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.dermalisys.R
import com.dermalisys.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        val imageUriString = intent.getStringExtra("image")
        val imageUri = Uri.parse(imageUriString)
        if (imageUri == null) {
            Glide.with(this).load(imageUriString).into(binding.imageView)
        } else {
            imageUri.let {
                Log.d("Image URI", "showImage: $it")
                binding.imageView.setImageURI(it)
            }
        }
        binding.tvDiseaseName.text = intent.getStringExtra("name")
        binding.tvLatinName.text = intent.getStringExtra("latinName")
        binding.tvDescription.text = intent.getStringExtra("description")
        binding.tvConfidenceScore.text = intent.getStringExtra("confidenceScore")
        binding.tvCaused1.text = intent.getStringExtra("cause1")
        binding.tvCaused2.text = intent.getStringExtra("cause2")
//        intent.getStringArrayListExtra("cause2")?.forEach {
//            binding.tvCaused2.text = "$it\n"
//        }
        binding.tvSymptoms1.text = intent.getStringExtra("symptom1")
        intent.getStringArrayListExtra("symptom2")?.forEach {
            var num = 1
            binding.tvSymptoms2.text = "$num. $it\n"
            num += 1
        }
        binding.tvTreatment1.text = intent.getStringExtra("zatAktif")
        binding.tvTreatment2.text = intent.getStringExtra("tipe")
        intent.getStringArrayListExtra("merk")?.forEach {
            binding.tvTreatment3.text = "\u2022 $it\n"
        }
    }
}