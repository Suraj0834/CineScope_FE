package com.cinescope.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cinescope.app.R
import com.cinescope.app.data.model.Movie
import com.cinescope.app.databinding.ItemMovieBinding
import com.cinescope.app.util.Constants
import com.cinescope.app.util.loadImage

class MoviesAdapter(
    private val onMovieClick: (Movie) -> Unit
) : ListAdapter<Movie, MoviesAdapter.MovieViewHolder>(MovieDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
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
        private val binding: ItemMovieBinding
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

                // Set genre
                if (!movie.genre.isNullOrEmpty()) {
                    tvGenre.text = movie.genre
                    dotSeparator1.visibility = android.view.View.VISIBLE
                } else {
                    tvGenre.text = "Movie"
                    dotSeparator1.visibility = android.view.View.VISIBLE
                }

                // Runtime not available in Movie model (only in MovieDetail)
                tvRuntime.visibility = android.view.View.GONE
                dotSeparator2.visibility = android.view.View.GONE

                // Optional: Show popularity/vote count
                if (movie.voteCount != null && movie.voteCount > 0) {
                    val votes = when {
                        movie.voteCount >= 1000 -> String.format("%.1fK votes", movie.voteCount / 1000.0)
                        else -> "${movie.voteCount} votes"
                    }
                    tvPopularity.text = votes
                    popularityLayout.visibility = android.view.View.VISIBLE
                } else {
                    popularityLayout.visibility = android.view.View.GONE
                }

                // Hide optional badges by default
                trendingBadge.visibility = android.view.View.GONE
                quickActionsLayout.visibility = android.view.View.GONE

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
