package com.cinescope.app.ui.favorites

import androidx.lifecycle.viewModelScope
import com.cinescope.app.data.model.FavoriteItem
import com.cinescope.app.data.repository.ProfileRepository
import com.cinescope.app.ui.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel : BaseViewModel() {
    
    private val repository = ProfileRepository()
    
    private val _favoritesState = MutableStateFlow<FavoritesState>(FavoritesState.Idle)
    val favoritesState: StateFlow<FavoritesState> = _favoritesState
    
    /**
     * Load user's favorites
     */
    fun loadFavorites() {
        _favoritesState.value = FavoritesState.Loading
        
        viewModelScope.launch {
            repository.getFavorites()
                .onSuccess { response ->
                    _favoritesState.value = FavoritesState.Success(response.favorites)
                }
                .onFailure { error ->
                    _favoritesState.value = FavoritesState.Error(
                        error.message ?: "Failed to load favorites"
                    )
                }
        }
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
}

sealed class FavoritesState {
    object Idle : FavoritesState()
    object Loading : FavoritesState()
    data class Success(val favorites: List<FavoriteItem>) : FavoritesState()
    data class Error(val message: String) : FavoritesState()
}
