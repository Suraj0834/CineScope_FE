package com.cinescope.app.ui.details

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cinescope.app.R
import com.cinescope.app.data.model.Video
import com.cinescope.app.databinding.ItemVideoBinding
import com.cinescope.app.util.loadImage

class VideosAdapter(
    private val onVideoClick: (Video) -> Unit
) : ListAdapter<Video, VideosAdapter.VideoViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)
        
        // Add animation
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in_slide_up)
        )
    }

    inner class VideoViewHolder(
        private val binding: ItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onVideoClick(getItem(position))
                }
            }
        }

        fun bind(video: Video) {
            binding.apply {
                tvVideoTitle.text = video.name
                
                // Load YouTube thumbnail
                // Max resolution: https://img.youtube.com/vi/<video_id>/maxresdefault.jpg
                // Standard: https://img.youtube.com/vi/<video_id>/hqdefault.jpg
                val thumbnailUrl = "https://img.youtube.com/vi/${video.key}/hqdefault.jpg"
                ivThumbnail.loadImage(thumbnailUrl)
            }
        }
    }

    private class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }
    }
}
