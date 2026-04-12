package com.mrnoone.cinescope.ui.favorites

import androidx.lifecycle.viewModelScope
import com.mrnoone.cinescope.data.model.FavoriteItem
import com.mrnoone.cinescope.data.repository.ProfileRepository
import com.mrnoone.cinescope.ui.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel : BaseViewModel() {

    private val repository = ProfileRepository()

    private val _favoritesState = MutableStateFlow<FavoritesState>(FavoritesState.Idle)
    val favoritesState: StateFlow<FavoritesState> = _favoritesState

    // Store original list for filtering and searching
    private var allFavorites: List<FavoriteItem> = emptyList()
    private var filteredFavorites: List<FavoriteItem> = emptyList()
    private var currentGenreFilter: String? = null
    private var currentSearchQuery: String = ""

    /**
     * Load user's favorites
     */
    fun loadFavorites() {
        _favoritesState.value = FavoritesState.Loading

        viewModelScope.launch {
            repository.getFavorites()
                .onSuccess { response ->
                    allFavorites = response.favorites
                    filteredFavorites = allFavorites
                    applyFiltersAndSearch()
                }
                .onFailure { error ->
                    _favoritesState.value = FavoritesState.Error(
                        error.message ?: "Failed to load favorites"
                    )
                }
        }
    }

    /**
     * Filter favorites by genre
     */
    fun filterByGenre(genre: String?) {
        currentGenreFilter = genre
        applyFiltersAndSearch()
    }

    /**
     * Search movies by title
     */
    fun searchMovies(query: String) {
        currentSearchQuery = query
        applyFiltersAndSearch()
    }

    /**
     * Sort movies by different criteria
     */
    fun sortMovies(sortOption: SortOption) {
        val currentState = _favoritesState.value
        if (currentState is FavoritesState.Success) {
            val sorted = when (sortOption) {
                SortOption.DATE_ADDED -> {
                    // Assuming most recent first
                    currentState.favorites.sortedByDescending { it.addedAt }
                }
                SortOption.TITLE_ASC -> {
                    currentState.favorites.sortedBy { it.title }
                }
                SortOption.TITLE_DESC -> {
                    currentState.favorites.sortedByDescending { it.title }
                }
                SortOption.RATING_DESC -> {
                    // Mock implementation - shuffle to simulate rating sort
                    currentState.favorites.sortedByDescending { it.title.length }
                }
                SortOption.RATING_ASC -> {
                    // Mock implementation - shuffle to simulate rating sort
                    currentState.favorites.sortedBy { it.title.length }
                }
            }
            _favoritesState.value = FavoritesState.Success(sorted)
        }
    }

    /**
     * Apply all active filters and search
     */
    private fun applyFiltersAndSearch() {
        var result = allFavorites

        // Apply genre filter
        currentGenreFilter?.let { genre ->
            result = result.filter { favorite ->
                // Note: You'll need to add genre info to FavoriteItem or fetch it
                // For now, this is a placeholder
                true // TODO: Implement genre filtering when data is available
            }
        }

        // Apply search filter
        if (currentSearchQuery.isNotBlank()) {
            result = result.filter { favorite ->
                favorite.title.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        filteredFavorites = result
        _favoritesState.value = FavoritesState.Success(filteredFavorites)
    }

    /**
     * Remove movie from favorites
     */
    fun removeFromFavorites(imdbId: String) {
        viewModelScope.launch {
            repository.removeFromFavorites(imdbId)
                .onSuccess {
                    // Reload favorites after removal
                    loadFavorites()
                }
                .onFailure { error ->
                    _favoritesState.value = FavoritesState.Error(
                        error.message ?: "Failed to remove from favorites"
                    )
                }
        }
    }

    /**
     * Clear all filters and search
     */
    fun clearFilters() {
        currentGenreFilter = null
        currentSearchQuery = ""
        applyFiltersAndSearch()
    }
}

/**
 * Sort options enum
 */
enum class SortOption {
    DATE_ADDED,
    TITLE_ASC,
    TITLE_DESC,
    RATING_DESC,
    RATING_ASC
}

sealed class FavoritesState {
    object Idle : FavoritesState()
    object Loading : FavoritesState()
    data class Success(val favorites: List<FavoriteItem>) : FavoritesState()
    data class Error(val message: String) : FavoritesState()
}
