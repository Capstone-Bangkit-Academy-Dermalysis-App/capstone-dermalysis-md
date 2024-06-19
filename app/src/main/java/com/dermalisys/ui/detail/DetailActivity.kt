package com.dermalisys.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dermalisys.data.remote.response.getuserpredict.DataItem
import com.dermalisys.databinding.ActivityDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        val detail = intent.getParcelableExtra<DataItem>(EXTRA_DATA) as DataItem
        setup(detail)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setup(item: DataItem) {


        val createdAt = item.createdAt
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(createdAt)
        val onlyDate = outputFormat.format(date!!)

        Glide.with(applicationContext)
            .load(item.uploadedImage)
            .into(binding.imageView)
        binding.tvDiseaseName.text = item.name
        binding.tvLatinName.text = item.latinName
        binding.tvDescription.text = item.description
        binding.tvConfidenceScore.text = String.format("%.2f%%", item.confidenceScore)

        binding.tvCaused1.text = item.cause.section1
        val cause2List = item.cause.section2
        val cause2String = cause2List.mapIndexed { index, cause2item ->
            "${index + 1}. $cause2item"
        }.joinToString(separator = "\n") ?: "".trim()
        binding.tvCaused2.text = cause2String

        binding.tvSymptoms1.text = item.symptom.section1
        val section2 = item.symptom.section2
        val result = section2.mapIndexed { index, section2item ->
            "${index + 1}. $section2item"
        }.joinToString("\n")
        binding.tvSymptoms2.text = result

        val treatment = item.treatment
        val listTreatment = treatment.mapIndexed { index, treatmentItem ->
            val merkTreatment = treatmentItem.merk
            val listMerkTreatment = merkTreatment.mapIndexed { _, merkItem ->
                "• $merkItem"
            }.joinToString("\n   ")
            "${index + 1}. ${treatmentItem.zatAktif}\n" +
                    "   ${treatmentItem.tipe}\n" +
                    "   $listMerkTreatment"
        }.joinToString("\n")
        binding.tvTreatment.text = listTreatment

        binding.tvDate.text = onlyDate

        binding.ivArrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}