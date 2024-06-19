package com.dermalisys.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.dermalisys.data.remote.response.getuserpredict.DataItem
import com.dermalisys.databinding.HistoryItemBinding
import com.dermalisys.ui.detail.DetailActivity
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : PagingDataAdapter<DataItem, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {



        @SuppressLint("DefaultLocale")
        fun bind(item: DataItem) {

            val createdAt = item.createdAt
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(createdAt)
            val onlyDate = outputFormat.format(date!!)

            binding.tvNamaPenyakit.text = item.name
            binding.tvConfidenceScore.text = String.format("%.1f%%", item.confidenceScore)
            binding.tvDeskripsiPenyakit.text = item.description
            binding.tvTglScan.text = onlyDate
            Glide.with(itemView.context)
                .load(item.uploadedImage)
                .into(binding.ivDiseaseImage)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_DATA, item)
                }
                itemView.context.startActivity(intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity)
                        .toBundle()
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}