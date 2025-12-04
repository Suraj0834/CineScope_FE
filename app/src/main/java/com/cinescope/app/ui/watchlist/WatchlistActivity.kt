package com.cinescope.app.ui.watchlist

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.cinescope.app.ailang.AiLang
import com.cinescope.app.databinding.ActivityWatchlistBinding
import com.cinescope.app.ui.details.MovieDetailActivity
import com.cinescope.app.ui.favorites.FavoritesActivity
import com.cinescope.app.ui.home.HomeActivity
import com.cinescope.app.ui.profile.ProfileActivity
import com.cinescope.app.util.Constants
import com.cinescope.app.util.gone
import com.cinescope.app.util.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import com.cinescope.app.R

class WatchlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWatchlistBinding
    private lateinit var viewModel: WatchlistViewModel
    private lateinit var adapter: WatchlistAdapter

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
        
        // Listen for language changes
        AiLang.addListener { recreate() }
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
                        binding.tvTotalHours.text = "${state.watchlist.size * 2}h"
                        
                        if (state.watchlist.isNotEmpty()) {
                            adapter.submitList(state.watchlist)
                            binding.rvWatchlist.visible()
                            binding.emptyState.gone()
                        } else {
                            binding.rvWatchlist.gone()
                            binding.emptyState.visible()
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
