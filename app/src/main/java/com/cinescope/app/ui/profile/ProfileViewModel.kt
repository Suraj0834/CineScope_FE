package com.cinescope.app.ui.profile

import androidx.lifecycle.viewModelScope
import com.cinescope.app.data.model.Movie
import com.cinescope.app.data.model.User
import com.cinescope.app.data.repository.ProfileRepository
import com.cinescope.app.ui.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(
        val user: User,
        val watchlist: List<Movie>,
        val favorites: List<Movie>
    ) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel : BaseViewModel() {
    
    private val repository = ProfileRepository()
    
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState
    
    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            
            try {
                val profileResult = repository.getProfile().getOrThrow()
                val watchlistResult = repository.getWatchlist().getOrThrow()
                val favoritesResult = repository.getFavorites().getOrThrow()
                
                if (profileResult.success && profileResult.data != null) {
                    _profileState.value = ProfileState.Success(
                        user = profileResult.data.user,
                        watchlist = watchlistResult.watchlist.map { 
                            Movie(imdbId = it.imdbId ?: "", title = it.title, posterPath = it.posterPath) 
                        },
                        favorites = favoritesResult.favorites.map { 
                            Movie(imdbId = it.imdbId, title = it.title, posterPath = it.posterPath) 
                        }
                    )
                } else {
                    _profileState.value = ProfileState.Error(profileResult.message)
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(
                    e.message ?: "Failed to load profile"
                )
            }
        }
    }
    
    fun updateProfile(name: String) {
        viewModelScope.launch {
            launchCatching(
                onSuccess = {
                    loadProfile()
                },
                onError = { error ->
                    _profileState.value = ProfileState.Error(
                        error.message ?: "Failed to update profile"
                    )
                }
            ) {
                repository.updateProfile(name)
            }
        }
    }
}
