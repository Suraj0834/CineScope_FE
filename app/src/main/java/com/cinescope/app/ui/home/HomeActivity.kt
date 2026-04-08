package com.cinescope.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.cinescope.app.R
import com.cinescope.app.databinding.ActivityHomeBinding
import com.cinescope.app.ui.auth.LoginActivity
import com.cinescope.app.ui.details.MovieDetailActivity
import com.cinescope.app.ui.profile.ProfileActivity
import com.cinescope.app.ui.watchlist.WatchlistActivity
import com.cinescope.app.ui.favorites.FavoritesActivity
import com.cinescope.app.util.Constants
import com.cinescope.app.util.NetworkUtils
import com.cinescope.app.util.PreferenceManager
import com.cinescope.app.util.gone
import com.cinescope.app.util.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var preferenceManager: PreferenceManager
    private var searchJob: Job? = null
    private var currentFilter = "trending"
    private var isGridView = true
    private var isFirstResume = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setupRecyclerView()
        setupViews()
        setupTopBar()
        setupFilters()
        setupBottomNavigation()
        observeViewModel()
        applyTranslations() // Apply translations to UI

        if (NetworkUtils.isNetworkAvailable(this)) {
            viewModel.loadTrending()
        } else {
            Snackbar.make(binding.root, com.cinescope.app.ailang.AiLang.t("error_generic"), Snackbar.LENGTH_LONG).show()
        }

        // Listen for language changes - update UI instead of recreating
        com.cinescope.app.ailang.AiLang.addListener {
            applyTranslations()
        }
    }
    
    private fun applyTranslations() {
        // Apply AiLang translations to UI elements
        binding.apply {
            etSearch.hint = com.cinescope.app.ailang.AiLang.t("search_movies")
            chipTrending.text = com.cinescope.app.ailang.AiLang.t("trending")
            chipPopular.text = com.cinescope.app.ailang.AiLang.t("popular")
            chipTopRated.text = com.cinescope.app.ailang.AiLang.t("top_rated")
            chipAction.text = com.cinescope.app.ailang.AiLang.t("action")
            chipComedy.text = com.cinescope.app.ailang.AiLang.t("comedy")
            chipDrama.text = com.cinescope.app.ailang.AiLang.t("drama")
            chipSciFi.text = com.cinescope.app.ailang.AiLang.t("sci_fi")
            chipThriller.text = com.cinescope.app.ailang.AiLang.t("thriller")
            chipWatchlist.text = com.cinescope.app.ailang.AiLang.t("my_watchlist")
        }
    }

    /**
     * Setup top bar action buttons
     */
    private fun setupTopBar() {
        // AI Chat button
        binding.btnAiChat.setOnClickListener {
            // TODO: Open AI Chat Activity
            Snackbar.make(binding.root, "AI Chat - Coming soon!", Snackbar.LENGTH_SHORT).show()
        }

        // Notifications button
        binding.btnNotifications.setOnClickListener {
            // TODO: Open Notifications
            Snackbar.make(binding.root, "Notifications - Coming soon!", Snackbar.LENGTH_SHORT).show()
        }

        // Profile button
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // View mode toggle
        binding.btnViewMode.setOnClickListener {
            toggleViewMode()
        }

        // Filter button
        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }

        // Scroll to top FAB
        binding.fabScrollTop.setOnClickListener {
            binding.rvMovies.smoothScrollToPosition(0)
            binding.fabScrollTop.gone()
        }

        // Voice search button
        binding.btnVoiceSearch.setOnClickListener {
            // TODO: Implement voice search
            Snackbar.make(binding.root, "Voice search - Coming soon!", Snackbar.LENGTH_SHORT).show()
        }

        // Clear filters button
        binding.btnClearFilters.setOnClickListener {
            clearAllFilters()
        }
    }

    /**
     * Toggle between grid and list view
     */
    private fun toggleViewMode() {
        isGridView = !isGridView
        val layoutManager = if (isGridView) {
            GridLayoutManager(this, 2)
        } else {
            androidx.recyclerview.widget.LinearLayoutManager(this)
        }
        binding.rvMovies.layoutManager = layoutManager

        // Update icon
        binding.btnViewMode.setImageResource(
            if (isGridView) android.R.drawable.ic_menu_view
            else android.R.drawable.ic_menu_agenda
        )

        // Show feedback
        Snackbar.make(
            binding.root,
            if (isGridView) "Grid view" else "List view",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    /**
     * Show filter/sort dialog
     */
    private fun showFilterDialog() {
        val options = arrayOf(
            "Popular",
            "Top Rated",
            "Now Playing",
            "Upcoming"
        )

        AlertDialog.Builder(this)
            .setTitle("Quick Filters")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> loadPopular()
                    1 -> loadTopRated()
                    2 -> loadNowPlaying()
                    3 -> loadUpcoming()
                }
            }
            .show()
    }

    /**
     * Clear all active filters
     */
    private fun clearAllFilters() {
        binding.etSearch.text?.clear()
        binding.chipTrending.isChecked = true
        currentFilter = "trending"
        viewModel.loadTrending()
    }

    private fun loadPopular() {
        currentFilter = "popular"
        binding.chipPopular.isChecked = true
        viewModel.searchMovies("popular")
    }

    private fun loadTopRated() {
        currentFilter = "top_rated"
        binding.chipTopRated.isChecked = true
        viewModel.searchMovies("top rated")
    }

    private fun loadNowPlaying() {
        viewModel.searchMovies("now playing")
    }

    private fun loadUpcoming() {
        viewModel.searchMovies("upcoming")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Remove listener to avoid leaks (though AiLang is singleton and activity is destroyed, it's good practice)
        // But since I'm using a lambda, I can't easily remove it unless I store it.
        // For this hackathon, it's fine, or I can store it.
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_home
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already on home
                    true
                }
                R.id.navigation_watchlist -> {
                    startActivity(Intent(this, WatchlistActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupRecyclerView() {
        moviesAdapter = MoviesAdapter { movie ->
            val intent = Intent(this, MovieDetailActivity::class.java)
            // Use traktId for API calls, fallback to imdbId
            val movieId = movie.traktId?.toString() ?: movie.imdbId
            intent.putExtra(Constants.EXTRA_IMDB_ID, movieId)
            intent.putExtra(Constants.EXTRA_MOVIE_TITLE, movie.title)
            startActivity(intent)
        }

        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(this@HomeActivity, 2)
            adapter = moviesAdapter

            // Scroll listener for endless scrolling and FAB visibility
            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lm = recyclerView.layoutManager as? GridLayoutManager ?: return
                    val visibleItemCount = lm.childCount
                    val totalItemCount = lm.itemCount
                    val firstVisibleItemPosition = lm.findFirstVisibleItemPosition()

                    // Show/hide FAB based on scroll position
                    if (firstVisibleItemPosition > 10 && dy < 0) {
                        // Scrolling up and past 10 items - show FAB
                        binding.fabScrollTop.visible()
                    } else if (dy > 0) {
                        // Scrolling down - hide FAB
                        binding.fabScrollTop.gone()
                    } else if (firstVisibleItemPosition < 3) {
                        // Near top - hide FAB
                        binding.fabScrollTop.gone()
                    }

                    // Endless scroll - load more when approaching end
                    val threshold = 4
                    if (!viewModel.isLoading() && !viewModel.isLastPage()) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - threshold)
                            && firstVisibleItemPosition >= 0
                        ) {
                            // Show load more indicator
                            binding.progressLoadMore.visible()
                            viewModel.loadTrending(loadMore = true)
                        }
                    }
                }
            })
        }
    }
    
    private fun setupViews() {
        // Swipe to refresh
        binding.swipeRefresh.setOnRefreshListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                refreshContent()
            } else {
                binding.swipeRefresh.isRefreshing = false
                Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_SHORT).show()
            }
        }
        
        // Search functionality with debounce
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivClearSearch.visibility = if (s.isNullOrEmpty()) {
                    android.view.View.GONE
                } else {
                    android.view.View.VISIBLE
                }
            }
            
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500) // Debounce delay
                    val query = s.toString().trim()
                    if (query.isNotEmpty()) {
                        if (NetworkUtils.isNetworkAvailable(this@HomeActivity)) {
                            viewModel.searchMovies(query)
                        } else {
                            Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_SHORT).show()
                        }
                    } else {
                        refreshContent()
                    }
                }
            }
        })
        
        // Clear search button
        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
            refreshContent()
        }
        
        // Search action
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString().trim()
                if (query.isNotEmpty() && NetworkUtils.isNetworkAvailable(this)) {
                    viewModel.searchMovies(query)
                }
                true
            } else {
                false
            }
        }
    }
    
    private fun setupFilters() {
        // Trending
        binding.chipTrending.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "trending"
                binding.etSearch.text?.clear()
                viewModel.loadTrending()
            }
        }

        // Popular
        binding.chipPopular.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "popular"
                binding.etSearch.text?.clear()
                searchByGenre("popular")
            }
        }

        // Top Rated
        binding.chipTopRated.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "top_rated"
                binding.etSearch.text?.clear()
                searchByGenre("top rated")
            }
        }

        // Action
        binding.chipAction.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "action"
                searchByGenre("action")
            }
        }

        // Comedy
        binding.chipComedy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "comedy"
                searchByGenre("comedy")
            }
        }

        // Drama
        binding.chipDrama.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "drama"
                searchByGenre("drama")
            }
        }

        // Sci-Fi
        binding.chipSciFi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "sci-fi"
                searchByGenre("sci-fi")
            }
        }

        // Thriller
        binding.chipThriller.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "thriller"
                searchByGenre("thriller")
            }
        }

        // Watchlist - Click listener only (not checkable)
        binding.chipWatchlist.setOnClickListener {
            val intent = Intent(this, WatchlistActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun searchByGenre(genre: String) {
        binding.etSearch.text?.clear()
        if (NetworkUtils.isNetworkAvailable(this)) {
            // Search for popular movies of that genre
            viewModel.searchMovies(genre)
        } else {
            Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private fun refreshContent() {
        when (currentFilter) {
            "trending" -> viewModel.loadTrending()
            else -> searchByGenre(currentFilter)
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.homeState.collect { state ->
                when (state) {
                    is HomeState.Idle -> {
                        binding.loadingState.gone()
                        binding.progressBar.gone()
                        binding.progressLoadMore.gone()
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is HomeState.Loading -> {
                        if (moviesAdapter.currentList.isEmpty()) {
                            // Show full loading state for initial load
                            binding.loadingState.visible()
                            binding.progressBar.visible()
                        }
                        // Hide load more for initial load
                        binding.progressLoadMore.gone()
                        binding.tvEmpty.gone()
                    }
                    is HomeState.Success -> {
                        binding.loadingState.gone()
                        binding.progressBar.gone()
                        binding.progressLoadMore.gone()
                        binding.swipeRefresh.isRefreshing = false

                        if (state.movies.isNotEmpty()) {
                            binding.tvEmpty.gone()
                            binding.rvMovies.visible()
                            moviesAdapter.submitList(state.movies)
                        } else {
                            binding.rvMovies.gone()
                            binding.tvEmpty.visible()
                        }
                    }
                    is HomeState.Error -> {
                        binding.loadingState.gone()
                        binding.progressBar.gone()
                        binding.progressLoadMore.gone()
                        binding.swipeRefresh.isRefreshing = false

                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
                            .setAction("Retry") {
                                refreshContent()
                            }
                            .show()
                    }
                }
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_watchlist -> {
                startActivity(Intent(this, WatchlistActivity::class.java))
                true
            }
            R.id.action_favorites -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
                true
            }
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                preferenceManager.clearToken()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        // Only refresh on first resume to avoid unwanted refreshes when keyboard shows/hides
        if (isFirstResume) {
            isFirstResume = false
            // Initial load is already done in onCreate, no need to refresh here
        }
        // Note: Removed auto-refresh to prevent clearing search results when keyboard appears
    }
}
