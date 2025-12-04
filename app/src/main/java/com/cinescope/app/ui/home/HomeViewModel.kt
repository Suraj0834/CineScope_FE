package com.cinescope.app.ui.home

import androidx.lifecycle.viewModelScope
import com.cinescope.app.data.model.Movie
import com.cinescope.app.data.repository.MoviesRepository
import com.cinescope.app.ui.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {
    
    private val repository = MoviesRepository()
    
    private val _homeState = MutableStateFlow<HomeState>(HomeState.Idle)
    val homeState: StateFlow<HomeState> = _homeState
    
    // Pagination state
    private var currentPage = 1
    private var isLoadingPage = false
    private var isLastPage = false
    private val accumulatedMovies = mutableListOf<Movie>()
    private val seenIds = mutableSetOf<String>() // Changed to String for imdbId

    // Expose simple helpers for the UI
    fun isLoading(): Boolean = isLoadingPage
    fun isLastPage(): Boolean = isLastPage
    
    /**
     * Load trending movies. If loadMore is true, load the next page and append results.
     */
    fun loadTrending(loadMore: Boolean = false, timeWindow: String = "day") {
        if (isLoadingPage) return

        _homeState.value = if (!loadMore) HomeState.Loading else _homeState.value

        if (loadMore) currentPage += 1 else {
            currentPage = 1
            accumulatedMovies.clear()
            seenIds.clear()
            isLastPage = false
        }

        isLoadingPage = true

        viewModelScope.launch {
            repository.getTrending(timeWindow, currentPage)
                .onSuccess { response ->
                    isLoadingPage = false
                    if (response.success && response.data != null) {
                        val movies = response.data.movies
                        // Filter out movies we've already seen to avoid duplicates
                        val newMovies = movies.filter { !seenIds.contains(it.imdbId) }
                        accumulatedMovies.addAll(newMovies)

                        // Track seen IDs
                        movies.forEach { seenIds.add(it.imdbId) }

                        // Check if last page - use hasMore field from API or fallback to empty results
                        isLastPage = !response.data.hasMore || movies.isEmpty()

                        _homeState.value = HomeState.Success(accumulatedMovies.toList())
                    } else {
                        _homeState.value = HomeState.Error(response.message)
                    }
                }
                .onFailure { error ->
                    isLoadingPage = false
                    _homeState.value = HomeState.Error(
                        error.message ?: "Failed to load movies. Please try again."
                    )
                }
        }
    }
    
    /**
     * Load popular movies
     */
    fun loadPopular(loadMore: Boolean = false) {
        if (isLoadingPage) return

        _homeState.value = if (!loadMore) HomeState.Loading else _homeState.value

        if (loadMore) currentPage += 1 else {
            currentPage = 1
            accumulatedMovies.clear()
            seenIds.clear()
            isLastPage = false
        }

        isLoadingPage = true

        viewModelScope.launch {
            repository.getPopular(currentPage)
                .onSuccess { response ->
                    isLoadingPage = false
                    if (response.success && response.data != null) {
                        val movies = response.data.movies
                        val newMovies = movies.filter { !seenIds.contains(it.imdbId) }
                        accumulatedMovies.addAll(newMovies)
                        movies.forEach { seenIds.add(it.imdbId) }

                        // Check if last page - use hasMore field from API or fallback to empty results
                        isLastPage = !response.data.hasMore || movies.isEmpty()

                        _homeState.value = HomeState.Success(accumulatedMovies.toList())
                    } else {
                        _homeState.value = HomeState.Error(response.message)
                    }
                }
                .onFailure { error ->
                    isLoadingPage = false
                    _homeState.value = HomeState.Error(
                        error.message ?: "Failed to load movies. Please try again."
                    )
                }
        }
    }
    
    /**
     * Load top rated movies
     */
    fun loadTopRated(loadMore: Boolean = false) {
        if (isLoadingPage) return

        _homeState.value = if (!loadMore) HomeState.Loading else _homeState.value

        if (loadMore) currentPage += 1 else {
            currentPage = 1
            accumulatedMovies.clear()
            seenIds.clear()
            isLastPage = false
        }

        isLoadingPage = true

        viewModelScope.launch {
            repository.getTopRated(currentPage)
                .onSuccess { response ->
                    isLoadingPage = false
                    if (response.success && response.data != null) {
                        val movies = response.data.movies
                        val newMovies = movies.filter { !seenIds.contains(it.imdbId) }
                        accumulatedMovies.addAll(newMovies)
                        movies.forEach { seenIds.add(it.imdbId) }

                        // Check if last page - use hasMore field from API or fallback to empty results
                        isLastPage = !response.data.hasMore || movies.isEmpty()

                        _homeState.value = HomeState.Success(accumulatedMovies.toList())
                    } else {
                        _homeState.value = HomeState.Error(response.message)
                    }
                }
                .onFailure { error ->
                    isLoadingPage = false
                    _homeState.value = HomeState.Error(
                        error.message ?: "Failed to load movies. Please try again."
                    )
                }
        }
    }
    
    /**
     * Search movies by title
     */
    fun searchMovies(query: String, page: Int = 1) {
        _homeState.value = HomeState.Loading
        
        viewModelScope.launch {
            repository.searchMovies(query, page)
                .onSuccess { response ->
                    if (response.success && response.data != null) {
                        _homeState.value = HomeState.Success(response.data.movies)
                    } else {
                        _homeState.value = HomeState.Error(response.message)
                    }
                }
                .onFailure { error ->
                    _homeState.value = HomeState.Error(
                        error.message ?: "Search failed. Please try again."
                    )
                }
        }
    }
    
    /**
     * Discover movies with filters
     */
    fun discoverMovies(
        genre: String? = null,
        year: Int? = null,
        sortBy: String = "popularity.desc",
        page: Int = 1
    ) {
        _homeState.value = HomeState.Loading
        
        viewModelScope.launch {
            repository.discoverMovies(page, genre, year, sortBy)
                .onSuccess { response ->
                    if (response.success && response.data != null) {
                        _homeState.value = HomeState.Success(response.data.movies)
                    } else {
                        _homeState.value = HomeState.Error(response.message)
                    }
                }
                .onFailure { error ->
                    _homeState.value = HomeState.Error(
                        error.message ?: "Discovery failed. Please try again."
                    )
                }
        }
    }
}

sealed class HomeState {
    object Idle : HomeState()
    object Loading : HomeState()
    data class Success(val movies: List<Movie>) : HomeState()
    data class Error(val message: String) : HomeState()
}
