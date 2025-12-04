package com.cinescope.app.ui.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cinescope.app.R
import com.cinescope.app.data.model.WatchlistItem
import com.cinescope.app.databinding.ItemWatchlistMovieBinding
import com.cinescope.app.util.Constants
import com.cinescope.app.util.loadImage

class WatchlistAdapter(
    private val onMovieClick: (WatchlistItem) -> Unit,
    private val onRemoveClick: (WatchlistItem) -> Unit
) : ListAdapter<WatchlistItem, WatchlistAdapter.WatchlistViewHolder>(WatchlistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistViewHolder {
        val binding = ItemWatchlistMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WatchlistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        
        // Add animation
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in_slide_up)
        )
    }

    inner class WatchlistViewHolder(
        private val binding: ItemWatchlistMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMovieClick(getItem(position))
                }
            }
            
            binding.btnRemove.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRemoveClick(getItem(position))
                }
            }
        }

        fun bind(item: WatchlistItem) {
            binding.apply {
                tvTitle.text = item.title
                // Year is not in WatchlistItem, could parse from addedAt or just hide/show added date
                tvYear.text = "Added: ${item.addedAt.take(10)}"

                val posterUrl = if (item.posterPath != null) {
                    if (item.posterPath.startsWith("http")) {
                        item.posterPath
                    } else {
                        "${Constants.TMDB_IMAGE_BASE_URL}${Constants.POSTER_SIZE_MEDIUM}${item.posterPath}"
                    }
                } else {
                    null
                }
                
                ivPoster.loadImage(posterUrl)
            }
        }
    }

    private class WatchlistDiffCallback : DiffUtil.ItemCallback<WatchlistItem>() {
        override fun areItemsTheSame(oldItem: WatchlistItem, newItem: WatchlistItem): Boolean {
            return oldItem.imdbId == newItem.imdbId
        }

        override fun areContentsTheSame(oldItem: WatchlistItem, newItem: WatchlistItem): Boolean {
            return oldItem == newItem
        }
    }
}
