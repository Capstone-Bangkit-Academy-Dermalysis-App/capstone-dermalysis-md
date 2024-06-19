package com.dermalisys.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.dermalisys.R
import com.dermalisys.data.remote.response.login.LoginOkResponse
import com.dermalisys.databinding.ActivityMainBinding
import com.dermalisys.ui.ViewModelFactory
import com.dermalisys.ui.profile.ProfileActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ImageAdapter
    private var list = ArrayList<ImageData>()
    private lateinit var dots: ArrayList<TextView>

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            var index = 0
            override fun run() {
                if (index == list.size)
                    index = 0
                Log.e("Runnable", "run: $index")
                binding.viewPager.setCurrentItem(index)
                index++
                handler.postDelayed(this, 2000)
            }
        }

        list.add(ImageData(R.drawable.img_slider1))
        list.add(ImageData(R.drawable.img_slider2))
        list.add(ImageData(R.drawable.img_slider3))

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

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
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
    }

    private fun selectedDot(position: Int) {
        for (i in 0 until list.size){
            if (i == position){
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
            dots[i].textSize = 35f
            binding.dotsIndicator.addView(dots[i])
        }
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
    override fun onStart() {
        super.onStart()
        handler.post(runnable)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }
}