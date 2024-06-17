package com.dermalisys.ui.preview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dermalisys.BuildConfig
import com.dermalisys.R
import com.dermalisys.data.utils.getImageUri
import com.dermalisys.data.utils.reduceFileImage
import com.dermalisys.data.utils.uriToFile
import com.dermalisys.databinding.ActivityPreviewBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.main.MainActivity
import com.dermalisys.ui.result.ResultActivity
import com.dermalisys.util.Result
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewBinding

    private var currentImageUri: Uri? = null

    private val viewModel: PreviewViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.btnCamera.setOnClickListener { startCamera() }

        binding.btnGallery.setOnClickListener { startGallery() }

        binding.btnUpload.setOnClickListener {
            if (currentImageUri == null) {
                showToast(getString(R.string.empty_image_warning))
            } else {
                showLoading(true)
                currentImageUri.let { uri ->
                    val imageFile = uri?.let { it1 -> uriToFile(it1, this@PreviewActivity).reduceFileImage() }
                    Log.d("Image Classification File", "showImage: ${imageFile?.path}")

                    val requestImageFile = imageFile?.asRequestBody("image/jpeg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData(
                        "file",
                        imageFile?.name,
                        requestImageFile!!
                    )

                    upload(multipartBody, uri)
                }
            }
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun upload(multipart: MultipartBody.Part, currentImage: Uri) {
        lifecycleScope.launch {
            viewModel.getSession().observe(this@PreviewActivity) {

                val signature = generateSignature("{}", SECRET_TOKEN)

                viewModel.predictWithUser(multipart, it.userId, signature, "access_token=${it.accessToken}").observe(this@PreviewActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)

                            val cause2 = ArrayList(result.data.data.cause.section2)
                            val symptom2 = ArrayList(result.data.data.symptom.section2!!)

                            val intent = Intent(this@PreviewActivity, ResultActivity::class.java).apply {
                                putExtra("image", currentImage.toString())
                                putExtra("name", result.data.data.name)
                                putExtra("latinName", result.data.data.latinName)
                                putExtra("confidenceScore", String.format("%.2f%%", result.data.data.confidenceScore))
                                putExtra("description", result.data.data.description)
                                putExtra("cause1", result.data.data.cause.section1)
                                putStringArrayListExtra("cause2", cause2)
                                putExtra("symptom1", result.data.data.symptom.section1)
                                putStringArrayListExtra("symptom2", symptom2)
                                result.data.data.treatment.forEach { treatmentItem ->
                                    val merk = ArrayList(treatmentItem.merk)

                                    putExtra("zatAktif", treatmentItem.zatAktif)
                                    putExtra("tipe", treatmentItem.tipe)
                                    putStringArrayListExtra("merk", merk)
                                }
                            }
                            startActivity(intent)
                            finish()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            setupFail(result.error)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun setupFail(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Failed")
            setMessage(message)
            setPositiveButton("Continue") { _, _ ->
                startActivity(Intent(this@PreviewActivity, MainActivity::class.java))
                finish()
            }
            create()
            show()
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val SECRET_TOKEN = BuildConfig.API_SECRET_TOKEN
    }
}