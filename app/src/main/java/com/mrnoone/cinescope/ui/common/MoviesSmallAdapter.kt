package com.mrnoone.cinescope.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrnoone.cinescope.R
import com.mrnoone.cinescope.data.model.Movie
import com.mrnoone.cinescope.databinding.ItemMovieSmallBinding
import com.mrnoone.cinescope.util.Constants
import com.mrnoone.cinescope.util.loadImage

/**
 * Small-sized movie adapter for use in movie detail (similar movies) 
 * and person detail (filmography) pages
 */
class MoviesSmallAdapter(
    private val onMovieClick: (Movie) -> Unit
) : ListAdapter<Movie, MoviesSmallAdapter.MovieViewHolder>(MovieDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieSmallBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
        
        // Add animation
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in_slide_up)
        )
    }
    
    inner class MovieViewHolder(
        private val binding: ItemMovieSmallBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMovieClick(getItem(position))
                }
            }
        }
        
        fun bind(movie: Movie) {
            binding.apply {
                tvTitle.text = movie.title
                tvYear.text = movie.displayYear

                // Format and show rating
                val rating = movie.voteAverage ?: movie.imdbRating ?: 0.0
                if (rating > 0) {
                    tvRating.text = String.format("%.1f", rating)
                    ratingBadge.visibility = android.view.View.VISIBLE
                } else {
                    ratingBadge.visibility = android.view.View.GONE
                }

                // Optional: Show genre if available
                if (!movie.genre.isNullOrEmpty()) {
                    tvGenre.text = movie.genre
                    tvGenre.visibility = android.view.View.VISIBLE
                } else {
                    tvGenre.visibility = android.view.View.GONE
                }

                // Hide optional badges by default
                trendingBadge.visibility = android.view.View.GONE
                bookmarkCard.visibility = android.view.View.GONE
                tvQuality.visibility = android.view.View.GONE

                // Load poster using TMDB URL or full URL (OMDb)
                val posterUrl = if (movie.posterPath != null) {
                    if (movie.posterPath.startsWith("http")) {
                        movie.posterPath
                    } else {
                        "${Constants.TMDB_IMAGE_BASE_URL}${Constants.POSTER_SIZE_MEDIUM}${movie.posterPath}"
                    }
                } else {
                    movie.poster // Fallback or placeholder
                }

                ivPoster.loadImage(posterUrl)
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
