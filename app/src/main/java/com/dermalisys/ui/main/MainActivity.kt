package com.dermalisys.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dermalisys.BuildConfig
import androidx.viewpager2.widget.ViewPager2
import com.dermalisys.R
import com.dermalisys.databinding.ActivityMainBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.adapter.HistoryAdapter
import com.dermalisys.ui.adapter.LoadingStateAdapter
import com.dermalisys.ui.login.LoginActivity
import com.dermalisys.ui.preview.PreviewActivity
import com.dermalisys.ui.profile.ProfileActivity
import com.dermalisys.util.Result
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ImageAdapter
    private var list = ArrayList<ImageData>()
    private lateinit var dots: ArrayList<TextView>

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var auth: FirebaseAuth

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

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            var index = 0
            override fun run() {
                if (index == list.size)
                    index = 0
                Log.e("Runnable", "run: $index")
                binding.viewPager.currentItem = index
                index++
                handler.postDelayed(this, 2000)
            }
        }

        list.add(ImageData(R.drawable.img_slider1))
        list.add(ImageData(R.drawable.img_slider2))
        list.add(ImageData(R.drawable.img_slider3))

        binding.homeIcon.setColorFilter(resources.getColor(R.color.blue))
        binding.homeTv.setTextColor(resources.getColor(R.color.blue))

        showLoading(false)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        binding.profileActivity.setOnClickListener {
            viewModel.getSession().observe(this) {
                if (it.isLogin || firebaseUser != null) {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                } else {
                    showLoading(true)
                    Toast.makeText(this, "You need to login", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }

        binding.cameraButton.setOnClickListener {
            startActivity(Intent(this, PreviewActivity::class.java))
            finish()
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectedDot(position)
                super.onPageSelected(position)
            }
        })

        adapter = ImageAdapter(list)
        binding.viewPager.adapter = adapter
        dots = ArrayList()
        setIndicator()
        setSetup()

        val layoutManager = LinearLayoutManager(this)
        binding.rvHistory.layoutManager = layoutManager

        val signature = generateSignature("{}", SECRET_TOKEN)

        viewModel.getSession().observe(this@MainActivity) { user ->
            if (user.isLogin) {
                lifecycleScope.launch {
                    showLoading(false)
                    val adapter = HistoryAdapter()
                    binding.rvHistory.adapter = adapter.withLoadStateFooter(
                        footer = LoadingStateAdapter {
                            adapter.retry()
                        }
                    )
                    viewModel.getHistory(signature, user.userId, "access_token=${user.accessToken}")
                        .observe(this@MainActivity) {
                            adapter.submitData(lifecycle, it)
                        }
                }
            } else {
                binding.tvEmptyHistory.visibility = View.VISIBLE
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun selectedDot(position: Int) {
        for (i in 0 until list.size) {
            if (i == position) {
                dots[i].setTextColor(resources.getColor(R.color.white))
            } else {
                dots[i].setTextColor(resources.getColor(R.color.blue))
            }
        }
    }

    private fun setIndicator() {
        for (i in 0 until list.size) {
            dots.add(TextView(this))
            dots[i].text = "●"
            dots[i].textSize = 25f
            binding.dotsIndicator.addView(dots[i])
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setSetup() {
        with(binding) {
            viewModel.getSession().observe(this@MainActivity) {
                if (it.name == ""){
                    tvUsername.text = "Hi!"
                } else {
                    tvUsername.text = "Hi, ${it.name}"
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

    override fun onStart() {
        super.onStart()
        handler.post(runnable)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }
}