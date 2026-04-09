package com.cinescope.app.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var adapter: MoviesAdapter

    private var isGridView = true
    private var currentSortOption = SortOption.DATE_ADDED
    private var isFirstResume = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupBottomNavigation()
        setupViews()
        setupFilterChips()
        observeViewModel()

        viewModel.loadFavorites()

        // Listen for language changes - update toolbar title instead of recreating
        AiLang.addListener {
            supportActionBar?.title = AiLang.t("my_favorites")
        }
    }

    override fun onResume() {
        super.onResume()
        // Skip first resume since onCreate already loads the data
        if (!isFirstResume) {
            viewModel.loadFavorites()
        }
        isFirstResume = false
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = AiLang.t("my_favorites")
        }

        // Setup toolbar buttons
        binding.btnSearch.setOnClickListener {
            showSearchDialog()
        }

        binding.btnViewSwitcher.setOnClickListener {
            toggleViewMode()
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
        // Swipe to refresh
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadFavorites()
        }

        // Empty state button
        binding.btnExploreMovies.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Sort button
        binding.btnSortOptions.setOnClickListener {
            showSortDialog()
        }

        // FAB quick action
        binding.fabQuickAction.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun setupFilterChips() {
        // Set up filter chips
        binding.chipAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.filterByGenre(null)
        }

        binding.chipAction.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.filterByGenre("Action")
        }

        binding.chipComedy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.filterByGenre("Comedy")
        }

        binding.chipDrama.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.filterByGenre("Drama")
        }

        binding.chipSciFi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.filterByGenre("Science Fiction")
        }

        binding.chipThriller.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.filterByGenre("Thriller")
        }
    }

    private fun showSearchDialog() {
        val searchView = SearchView(this)
        searchView.queryHint = getString(R.string.search_movies)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.search_movies)
            .setView(searchView)
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchMovies(it) }
                dialog.dismiss()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {
                        viewModel.searchMovies("")
                    }
                }
                return true
            }
        })

        dialog.show()
        searchView.requestFocus()
    }

    private fun showSortDialog() {
        val options = arrayOf(
            getString(R.string.date_added),
            "Title (A-Z)",
            "Title (Z-A)",
            "Rating (High to Low)",
            "Rating (Low to High)"
        )

        val currentIndex = when (currentSortOption) {
            SortOption.DATE_ADDED -> 0
            SortOption.TITLE_ASC -> 1
            SortOption.TITLE_DESC -> 2
            SortOption.RATING_DESC -> 3
            SortOption.RATING_ASC -> 4
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.sort_by)
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                currentSortOption = when (which) {
                    0 -> SortOption.DATE_ADDED
                    1 -> SortOption.TITLE_ASC
                    2 -> SortOption.TITLE_DESC
                    3 -> SortOption.RATING_DESC
                    4 -> SortOption.RATING_ASC
                    else -> SortOption.DATE_ADDED
                }

                binding.btnSortOptions.text = options[which]
                viewModel.sortMovies(currentSortOption)
                dialog.dismiss()
            }
            .show()
    }

    private fun toggleViewMode() {
        isGridView = !isGridView

        binding.rvFavorites.layoutManager = if (isGridView) {
            GridLayoutManager(this, 2)
        } else {
            LinearLayoutManager(this)
        }

        // Update icon (you can create different icons for grid/list views)
        binding.btnViewSwitcher.setImageResource(
            if (isGridView) android.R.drawable.ic_menu_view
            else android.R.drawable.ic_menu_agenda
        )
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
                        binding.loadingState.visible()
                        binding.progressBar.visible()
                        binding.emptyState.gone()
                        binding.rvFavorites.gone()
                        binding.statsCard.gone()
                        binding.filterSortSection.gone()
                    }
                    is FavoritesState.Success -> {
                        binding.loadingState.gone()
                        binding.progressBar.gone()
                        binding.swipeRefresh.isRefreshing = false

                        // Calculate and update stats
                        val totalMovies = state.favorites.size
                        val avgRating = calculateAverageRating(state.favorites)
                        val watchTime = calculateWatchTime(state.favorites)

                        binding.tvTotalFavorites.text = totalMovies.toString()
                        binding.tvAvgRating.text = String.format("%.1f", avgRating)
                        binding.tvWatchTime.text = formatWatchTime(watchTime)

                        if (state.favorites.isNotEmpty()) {
                            // Map FavoriteItem to Movie
                            val movies = state.favorites.map { item ->
                                Movie(
                                    imdbId = item.imdbId,
                                    title = item.title,
                                    posterPath = item.posterPath,
                                    year = item.addedAt.take(4)
                                )
                            }
                            adapter.submitList(movies)
                            binding.rvFavorites.visible()
                            binding.statsCard.visible()
                            binding.filterSortSection.visible()
                            binding.emptyState.gone()
                        } else {
                            binding.rvFavorites.gone()
                            binding.statsCard.gone()
                            binding.filterSortSection.gone()
                            binding.emptyState.visible()
                        }
                    }
                    is FavoritesState.Error -> {
                        binding.loadingState.gone()
                        binding.progressBar.gone()
                        binding.swipeRefresh.isRefreshing = false
                        Snackbar.make(
                            binding.root,
                            state.message,
                            Snackbar.LENGTH_LONG
                        ).setAction("Retry") {
                            viewModel.loadFavorites()
                        }.show()
                    }
                    else -> {}
                }
            }
        }
    }

    /**
     * Calculate average rating from favorites list
     * Mock implementation - in production, fetch actual ratings from API
     */
    private fun calculateAverageRating(favorites: List<com.cinescope.app.data.model.FavoriteItem>): Double {
        if (favorites.isEmpty()) return 0.0
        // Mock rating between 7.0 and 9.0
        return 8.5
    }

    /**
     * Calculate total watch time from favorites (mock implementation)
     * In real app, you would get runtime from movie details
     */
    private fun calculateWatchTime(favorites: List<com.cinescope.app.data.model.FavoriteItem>): Int {
        // Mock: assume average movie is 120 minutes
        return favorites.size * 120
    }

    /**
     * Format watch time in hours
     */
    private fun formatWatchTime(minutes: Int): String {
        val hours = minutes / 60
        return when {
            hours == 0 -> "${minutes}m"
            hours < 24 -> "${hours}h"
            else -> "${hours / 24}d"
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
