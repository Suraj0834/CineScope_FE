package com.cinescope.app.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.cinescope.app.R
import com.cinescope.app.ailang.AiLang
import com.cinescope.app.data.model.Movie
import com.cinescope.app.databinding.ActivityFavoritesBinding
import com.cinescope.app.ui.details.MovieDetailActivity
import com.cinescope.app.ui.home.HomeActivity
import com.cinescope.app.ui.home.MoviesAdapter
import com.cinescope.app.ui.profile.ProfileActivity
import com.cinescope.app.ui.watchlist.WatchlistActivity
import com.cinescope.app.util.Constants
import com.cinescope.app.util.gone
import com.cinescope.app.util.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var adapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupBottomNavigation()
        setupViews()
        observeViewModel()

        viewModel.loadFavorites()
        
        // Listen for language changes
        AiLang.addListener { recreate() }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = AiLang.t("my_favorites")
        }
    }

    private fun setupRecyclerView() {
        adapter = MoviesAdapter { movie ->
            val intent = Intent(this, MovieDetailActivity::class.java)
            // Use traktId for API calls, fallback to imdbId
            val movieId = movie.traktId?.toString() ?: movie.imdbId
            intent.putExtra(Constants.EXTRA_IMDB_ID, movieId)
            intent.putExtra(Constants.EXTRA_MOVIE_TITLE, movie.title)
            startActivity(intent)
        }

        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(this@FavoritesActivity, 2)
            adapter = this@FavoritesActivity.adapter
        }
    }
    
    private fun setupViews() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadFavorites()
        }
        
        binding.btnExploreMovies.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_favorites
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_watchlist -> {
                    startActivity(Intent(this, WatchlistActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_favorites -> true
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.favoritesState.collect { state ->
                when (state) {
                    is FavoritesState.Loading -> {
                        binding.progressBar.visible()
                    }
                    is FavoritesState.Success -> {
                        binding.progressBar.gone()
                        binding.swipeRefresh.isRefreshing = false
                        
                        // Update stats
                        binding.tvTotalFavorites.text = state.favorites.size.toString()
                        // Mock rating
                        binding.tvAvgRating.text = "8.5"
                        
                        if (state.favorites.isNotEmpty()) {
                            // Map FavoriteItem to Movie
                            val movies = state.favorites.map { item ->
                                Movie(
                                    imdbId = item.imdbId ?: "",
                                    title = item.title,
                                    posterPath = item.posterPath,
                                    year = item.addedAt?.take(4)
                                )
                            }
                            adapter.submitList(movies)
                            binding.rvFavorites.visible()
                            binding.emptyState.gone()
                        } else {
                            binding.rvFavorites.gone()
                            binding.emptyState.visible()
                        }
                    }
                    is FavoritesState.Error -> {
                        binding.progressBar.gone()
                        binding.swipeRefresh.isRefreshing = false
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
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
