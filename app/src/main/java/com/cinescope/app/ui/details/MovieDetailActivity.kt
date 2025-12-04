package com.cinescope.app.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cinescope.app.R
import com.cinescope.app.ailang.AiLang
import com.cinescope.app.databinding.ActivityMovieDetailBinding
import com.cinescope.app.ui.chat.ChatActivity
import com.cinescope.app.ui.common.MoviesSmallAdapter
import com.cinescope.app.util.Constants
import com.cinescope.app.util.gone
import com.cinescope.app.util.loadImage
import com.cinescope.app.util.visible
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MovieDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMovieDetailBinding
    private lateinit var viewModel: MovieDetailViewModel
    private lateinit var castAdapter: CastAdapter
    private lateinit var videosAdapter: VideosAdapter
    private lateinit var similarAdapter: MoviesSmallAdapter
    private lateinit var sourcesAdapter: SourcesAdapter
    
    private var imdbId: String = ""
    private var movieTitle: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        imdbId = intent.getStringExtra(Constants.EXTRA_IMDB_ID) ?: ""
        movieTitle = intent.getStringExtra(Constants.EXTRA_MOVIE_TITLE) ?: ""
        
        viewModel = ViewModelProvider(this)[MovieDetailViewModel::class.java]
        
        setupToolbar()
        setupRecyclerViews()
        setupViews()
        observeViewModel()
        applyTranslations()
        
        if (imdbId.isNotEmpty()) {
            viewModel.loadMovieDetails(imdbId)
        } else {
            Snackbar.make(binding.root, AiLang.t("error_generic"), Snackbar.LENGTH_LONG).show()
            finish()
        }
        
        // Listen for language changes
        AiLang.addListener { recreate() }
    }
    
    private fun applyTranslations() {
        binding.apply {
            tvOverviewLabel.text = AiLang.t("overview")
            tvCastLabel.text = AiLang.t("cast")
            btnViewAllCast.text = AiLang.t("view_all")
            tvVideosLabel.text = AiLang.t("trailers_videos")
            tvSimilarLabel.text = AiLang.t("similar_movies")
            btnAiSummary.text = AiLang.t("get_ai_summary")
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "" // Title is in the content
        }
    }
    
    private fun setupRecyclerViews() {
        // Cast Adapter
        castAdapter = CastAdapter { castMember ->
            // Open Person Detail Activity
            val intent = Intent(this, com.cinescope.app.ui.person.PersonDetailActivity::class.java)
            intent.putExtra(Constants.EXTRA_PERSON_ID, castMember.id)
            intent.putExtra(Constants.EXTRA_PERSON_NAME, castMember.name)
            startActivity(intent)
        }
        binding.rvCast.apply {
            layoutManager = LinearLayoutManager(this@MovieDetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = castAdapter
        }
        
        // Videos Adapter
        videosAdapter = VideosAdapter { video ->
            // Open YouTube
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${video.key}"))
            startActivity(intent)
        }
        binding.rvVideos.apply {
            layoutManager = LinearLayoutManager(this@MovieDetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = videosAdapter
        }
        
        // Similar Movies Adapter (small size)
        similarAdapter = MoviesSmallAdapter { movie ->
            val intent = Intent(this, MovieDetailActivity::class.java)
            // Use traktId for API calls, fallback to imdbId
            val movieId = movie.traktId?.toString() ?: movie.imdbId
            intent.putExtra(Constants.EXTRA_IMDB_ID, movieId)
            intent.putExtra(Constants.EXTRA_MOVIE_TITLE, movie.title)
            startActivity(intent)
        }
        binding.rvSimilar.apply {
            layoutManager = LinearLayoutManager(this@MovieDetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = similarAdapter
        }

        // Streaming Sources Adapter
        sourcesAdapter = SourcesAdapter()
        binding.rvWatchSources.apply {
            layoutManager = LinearLayoutManager(this@MovieDetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = sourcesAdapter
        }
    }
    
    private fun setupViews() {
        binding.btnAiSummary.setOnClickListener {
            viewModel.getAiSummary()
        }
        
        binding.btnWatchlist.setOnClickListener {
            val isInWatchlist = (viewModel.statusState.value as? MovieStatusState.Success)?.isInWatchlist ?: false
            if (isInWatchlist) {
                viewModel.removeFromWatchlist()
            } else {
                viewModel.addToWatchlist()
            }
        }
        
        binding.btnFavorite.setOnClickListener {
            val isFavorite = (viewModel.statusState.value as? MovieStatusState.Success)?.isInFavorites ?: false
            if (isFavorite) {
                viewModel.removeFromFavorites()
            } else {
                viewModel.addToFavorites()
            }
        }
        
        binding.fabChat.setOnClickListener {
            openChatActivity()
        }
        
        binding.btnViewAllCast.setOnClickListener {
            // Could open a full cast list activity, for now just show a toast or expand
            Snackbar.make(binding.root, "Full cast list coming soon", Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private fun openChatActivity() {
        val movieDetail = (viewModel.detailState.value as? MovieDetailState.Success)?.movieDetail ?: return
        
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra(Constants.EXTRA_MOVIE_TITLE, movieDetail.title)
            // Pass minimal info, chat can fetch more if needed or just use context
            putExtra("imdbId", movieDetail.imdbId)
        }
        startActivity(intent)
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.detailState.collect { state ->
                when (state) {
                    is MovieDetailState.Idle -> binding.progressBar.gone()
                    is MovieDetailState.Loading -> binding.progressBar.visible()
                    is MovieDetailState.Success -> {
                        binding.progressBar.gone()
                        displayMovieDetail(state.movieDetail)
                    }
                    is MovieDetailState.Error -> {
                        binding.progressBar.gone()
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.statusState.collect { state ->
                when (state) {
                    is MovieStatusState.Success -> {
                        updateWatchlistButton(state.isInWatchlist)
                        updateFavoriteButton(state.isInFavorites)
                    }
                    else -> {}
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.summaryState.collect { state ->
                when (state) {
                    is SummaryState.Loading -> binding.btnAiSummary.isEnabled = false
                    is SummaryState.Success -> {
                        binding.btnAiSummary.isEnabled = true
                        showAiSummaryDialog(state.summary)
                    }
                    is SummaryState.Error -> {
                        binding.btnAiSummary.isEnabled = true
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    else -> binding.btnAiSummary.isEnabled = true
                }
            }
        }
    }
    
    private fun displayMovieDetail(detail: com.cinescope.app.data.model.MovieDetail) {
        binding.apply {
            // Poster URL (used for both poster and backdrop fallback)
            val posterUrl = if (detail.posterPath != null) {
                if (detail.posterPath.startsWith("http")) {
                    detail.posterPath
                } else {
                    "${Constants.TMDB_IMAGE_BASE_URL}${Constants.POSTER_SIZE_MEDIUM}${detail.posterPath}"
                }
            } else {
                null
            }
            
            // Backdrop - use backdrop if available, otherwise fallback to poster
            val backdropUrl = if (detail.backdropPath != null) {
                if (detail.backdropPath.startsWith("http")) {
                    detail.backdropPath
                } else {
                    "${Constants.TMDB_IMAGE_BASE_URL}${Constants.BACKDROP_SIZE_LARGE}${detail.backdropPath}"
                }
            } else {
                // Fallback to poster image for backdrop
                posterUrl
            }
            ivBackdrop.loadImage(backdropUrl)

            // Poster
            ivPoster.loadImage(posterUrl)
            
            // Title & Info
            tvTitle.text = detail.title
            tvYear.text = detail.displayYear
            tvRuntime.text = detail.displayRuntime
            tvRating.text = detail.displayRating
            tvOverview.text = detail.overview
            
            // Genres
            chipGroupGenres.removeAllViews()
            detail.genres?.take(3)?.forEach { genre ->
                val chip = Chip(this@MovieDetailActivity).apply {
                    text = genre.name
                    textSize = 11f
                    setChipBackgroundColorResource(R.color.md_theme_surfaceVariant)
                    setTextColor(getColor(R.color.md_theme_onSurfaceVariant))
                    chipMinHeight = 28f
                }
                chipGroupGenres.addView(chip)
            }
            
            // Cast
            if (!detail.credits?.cast.isNullOrEmpty()) {
                castAdapter.submitList(detail.credits?.cast)
                rvCast.visible()
            } else {
                rvCast.gone()
            }
            
            // Videos
            val trailers = detail.videos?.filter { it.site == "YouTube" && it.type == "Trailer" }
            if (!trailers.isNullOrEmpty()) {
                videosAdapter.submitList(trailers)
                rvVideos.visible()
            } else {
                rvVideos.gone()
            }
            
            // Similar
            if (!detail.similar.isNullOrEmpty()) {
                similarAdapter.submitList(detail.similar)
                rvSimilar.visible()
            } else {
                rvSimilar.gone()
            }

            // Streaming Sources (Watchmode)
            if (!detail.watchmodeSources.isNullOrEmpty()) {
                sourcesAdapter.submitList(detail.watchmodeSources)
                layoutWatchSources.visible()
            } else {
                layoutWatchSources.gone()
            }
        }
    }
    
    private fun updateWatchlistButton(isInWatchlist: Boolean) {
        binding.btnWatchlist.apply {
            if (isInWatchlist) {
                text = getString(R.string.remove_from_watchlist)
                setIconResource(android.R.drawable.star_big_on)
            } else {
                text = getString(R.string.add_to_watchlist)
                setIconResource(android.R.drawable.btn_star)
            }
        }
    }
    
    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.btnFavorite.apply {
            if (isFavorite) {
                text = getString(R.string.favorited)
                setIconResource(android.R.drawable.btn_star_big_on)
                setBackgroundColor(getColor(R.color.md_theme_tertiaryContainer))
                setTextColor(getColor(R.color.md_theme_onTertiaryContainer))
            } else {
                text = getString(R.string.favorite)
                setIconResource(android.R.drawable.btn_star_big_off)
                // Reset style to outlined if needed, or just toggle icon/text
            }
        }
    }
    
    private fun showAiSummaryDialog(summary: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_ai_summary, null)
        val tvSummaryContent = dialogView.findViewById<android.widget.TextView>(R.id.tvSummaryContent)
        tvSummaryContent.text = summary
        
        MaterialAlertDialogBuilder(this, R.style.Widget_Cinescope_AlertDialog)
            .setTitle("🤖 AI Movie Summary")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .show()
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
