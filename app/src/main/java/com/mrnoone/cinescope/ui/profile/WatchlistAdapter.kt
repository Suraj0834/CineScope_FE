package com.mrnoone.cinescope.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrnoone.cinescope.data.model.Movie
import com.mrnoone.cinescope.databinding.ItemWatchlistMovieBinding
import com.mrnoone.cinescope.util.loadImage

class WatchlistAdapter(
    private val onMovieClick: (Movie) -> Unit
) : ListAdapter<Movie, WatchlistAdapter.WatchlistViewHolder>(MovieDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistViewHolder {
        val binding = ItemWatchlistMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WatchlistViewHolder(binding, onMovieClick)
    }
    
    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class WatchlistViewHolder(
        private val binding: ItemWatchlistMovieBinding,
        private val onMovieClick: (Movie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(movie: Movie) {
            binding.apply {
                ivPoster.loadImage(movie.poster)
                tvTitle.text = movie.title
                tvYear.text = movie.year
                
                root.setOnClickListener {
                    onMovieClick(movie)
                }
            }
        }
    }
    
    private class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.imdbId == newItem.imdbId
        }
        
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}
