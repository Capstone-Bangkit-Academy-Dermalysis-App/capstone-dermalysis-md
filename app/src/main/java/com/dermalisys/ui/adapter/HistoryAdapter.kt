package com.dermalisys.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dermalisys.data.remote.response.getuserpredict.DataItem
import com.dermalisys.databinding.HistoryItemBinding
import com.dermalisys.ui.result.ResultActivity

class HistoryAdapter : PagingDataAdapter<DataItem, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItem) {

            val cause2 = ArrayList(item.cause.section2)
            val symptom2 = ArrayList(item.symptom.section2)

            binding.tvNamaPenyakit.text = item.name
            binding.tvDeskripsiPenyakit.text = item.description
            binding.tvTglScan.text = item.createdAt
            Glide.with(itemView.context)
                .load(item.uploadedImage)
                .into(binding.ivDiseaseImage)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ResultActivity::class.java).apply {
                    putExtra("image", item.uploadedImage)
                    putExtra("name", item.name)
                    putExtra("latinName", item.latinName)
                    putExtra("confidenceScore", String.format("%.2f%%", item.confidenceScore))
                    putExtra("description", item.description)
                    putExtra("cause1", item.cause.section1)
                    putStringArrayListExtra("cause2", cause2)
                    putExtra("symptom1", item.symptom.section1)
                    putStringArrayListExtra("symptom2", symptom2)
                    item.treatment.forEach { treatmentItem ->
                        val merk = ArrayList(treatmentItem.merk)

                        putExtra("zatAktif", treatmentItem.zatAktif)
                        putExtra("tipe", treatmentItem.tipe)
                        putStringArrayListExtra("merk", merk)
                    }
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