package com.dermalisys.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dermalisys.databinding.SlideItemBinding

class ImageAdapter(private val images: List<ImageData>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    inner class ImageViewHolder(itemView: SlideItemBinding) : RecyclerView.ViewHolder(itemView.root) {

        private val binding = itemView
        fun bind(data: ImageData) {
            with(binding) {
                Glide.with(itemView)
                    .load(data.image)
                    .into(ivSlider)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(SlideItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size
}