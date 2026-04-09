package com.cinescope.app.ui.watchlist

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cinescope.app.R
import com.cinescope.app.ailang.AiLang
import com.cinescope.app.databinding.ActivityWatchlistBinding
import com.cinescope.app.ui.details.MovieDetailActivity
import com.cinescope.app.ui.favorites.FavoritesActivity
import com.cinescope.app.ui.home.HomeActivity
import com.cinescope.app.ui.profile.ProfileActivity
import com.cinescope.app.util.Constants
import com.cinescope.app.util.gone
import com.cinescope.app.util.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class WatchlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWatchlistBinding
    private lateinit var viewModel: WatchlistViewModel
    private lateinit var adapter: WatchlistAdapter

    private var isGridView = true
    private var isFirstResume = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WatchlistViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupBottomNavigation()
        setupViews()
        observeViewModel()

        viewModel.loadWatchlist()
        
        // Listen for language changes - update toolbar title instead of recreating
        AiLang.addListener {
            supportActionBar?.title = AiLang.t("my_watchlist")
        }
    }

    override fun onResume() {
        super.onResume()
        // Skip first resume since onCreate already loads the data
        if (!isFirstResume) {
            viewModel.loadWatchlist()
        }
        isFirstResume = false
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = AiLang.t("my_watchlist")
        }
    }

    private fun setupRecyclerView() {
        adapter = WatchlistAdapter(
            onMovieClick = { item ->
                val intent = Intent(this, MovieDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_IMDB_ID, item.imdbId)
                intent.putExtra(Constants.EXTRA_MOVIE_TITLE, item.title)
                startActivity(intent)
            },
            onRemoveClick = { item ->
                viewModel.removeFromWatchlist(item.imdbId ?: "")
                Snackbar.make(binding.root, "${item.title} removed from watchlist", Snackbar.LENGTH_SHORT).show()
            }
        )

        binding.rvWatchlist.apply {
            layoutManager = GridLayoutManager(this@WatchlistActivity, 2)
            adapter = this@WatchlistActivity.adapter
        }
    }
    
    private fun setupViews() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadWatchlist()
        }

        binding.btnBrowseMovies.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // View mode toggle
        binding.btnViewMode.setOnClickListener {
            toggleViewMode()
        }

        // Search button
        binding.btnSearch.setOnClickListener {
            Snackbar.make(binding.root, "Search functionality coming soon", Snackbar.LENGTH_SHORT).show()
        }

        // Filter/Sort button
        binding.btnFilter.setOnClickListener {
            showSortDialog()
        }

        // FAB scroll to top
        binding.fabScrollTop.setOnClickListener {
            binding.rvWatchlist.smoothScrollToPosition(0)
        }

        // Show/hide FAB based on scroll
        binding.rvWatchlist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager
                val firstVisiblePosition = when (layoutManager) {
                    is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                    is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                    else -> 0
                }

                if (firstVisiblePosition > 3) {
                    binding.fabScrollTop.show()
                } else {
                    binding.fabScrollTop.hide()
                }
            }
        })
    }

    /**
     * Toggle between grid and list view
     */
    private fun toggleViewMode() {
        isGridView = !isGridView
        binding.rvWatchlist.layoutManager = if (isGridView) {
            GridLayoutManager(this, 2)
        } else {
            LinearLayoutManager(this)
        }

        // Update icon
        binding.btnViewMode.setIconResource(
            if (isGridView) android.R.drawable.ic_menu_view else android.R.drawable.ic_menu_agenda
        )

        Snackbar.make(
            binding.root,
            if (isGridView) "Grid view" else "List view",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    /**
     * Show sort dialog
     */
    private fun showSortDialog() {
        val sortOptions = arrayOf(
            "Date Added (Newest)",
            "Date Added (Oldest)",
            "Title (A-Z)",
            "Title (Z-A)",
            "Rating (High to Low)",
            "Rating (Low to High)"
        )

        MaterialAlertDialogBuilder(this)
            .setTitle("Sort Watchlist")
            .setItems(sortOptions) { _, which ->
                val sortMessage = when (which) {
                    0 -> "Sorted by newest first"
                    1 -> "Sorted by oldest first"
                    2 -> "Sorted by title A-Z"
                    3 -> "Sorted by title Z-A"
                    4 -> "Sorted by rating (high to low)"
                    5 -> "Sorted by rating (low to high)"
                    else -> "Sorted"
                }
                Snackbar.make(binding.root, sortMessage, Snackbar.LENGTH_SHORT).show()
                // TODO: Implement actual sorting logic
            }
            .show()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_watchlist
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_watchlist -> true
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

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.watchlistState.collect { state ->
                when (state) {
                    is WatchlistState.Loading -> {
                        binding.progressBar.visible()
                    }
                    is WatchlistState.Success -> {
                        binding.progressBar.gone()
                        binding.swipeRefresh.isRefreshing = false

                        // Update stats
                        binding.tvTotalMovies.text = state.watchlist.size.toString()
                        // Mock hours calculation (e.g., avg 2h per movie)
                        val totalHours = state.watchlist.size * 2
                        binding.tvTotalHours.text = "${totalHours}h"

                        // Calculate average rating (mock - assume 8.5 average)
                        val avgRating = if (state.watchlist.isNotEmpty()) 8.5 else 0.0
                        binding.tvAvgRating.text = String.format("%.1f", avgRating)

                        if (state.watchlist.isNotEmpty()) {
                            adapter.submitList(state.watchlist)
                            binding.rvWatchlist.visible()
                            binding.emptyState.gone()
                            binding.statsCard.visible()
                        } else {
                            binding.rvWatchlist.gone()
                            binding.emptyState.visible()
                            binding.statsCard.gone()
                        }
                    }
                    is WatchlistState.Error -> {
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
