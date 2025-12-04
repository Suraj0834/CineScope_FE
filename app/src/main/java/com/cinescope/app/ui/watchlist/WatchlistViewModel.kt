package com.cinescope.app.ui.watchlist

import androidx.lifecycle.viewModelScope
import com.cinescope.app.data.model.WatchlistItem
import com.cinescope.app.data.repository.ProfileRepository
import com.cinescope.app.ui.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WatchlistViewModel : BaseViewModel() {
    
    private val repository = ProfileRepository()
    
    private val _watchlistState = MutableStateFlow<WatchlistState>(WatchlistState.Idle)
    val watchlistState: StateFlow<WatchlistState> = _watchlistState
    
    /**
     * Load user's watchlist
     */
    fun loadWatchlist() {
        _watchlistState.value = WatchlistState.Loading
        
        viewModelScope.launch {
            repository.getWatchlist()
                .onSuccess { response ->
                    _watchlistState.value = WatchlistState.Success(response.watchlist)
                }
                .onFailure { error ->
                    _watchlistState.value = WatchlistState.Error(
                        error.message ?: "Failed to load watchlist"
                    )
                }
        }
    }
    
    /**
     * Remove movie from watchlist
     */
    fun removeFromWatchlist(imdbId: String) {
        viewModelScope.launch {
            repository.removeFromWatchlist(imdbId)
                .onSuccess {
                    // Reload watchlist after removal
                    loadWatchlist()
                }
                .onFailure { error ->
                    _watchlistState.value = WatchlistState.Error(
                        error.message ?: "Failed to remove from watchlist"
                    )
                }
        }
    }
}

sealed class WatchlistState {
    object Idle : WatchlistState()
    object Loading : WatchlistState()
    data class Success(val watchlist: List<WatchlistItem>) : WatchlistState()
    data class Error(val message: String) : WatchlistState()
}
