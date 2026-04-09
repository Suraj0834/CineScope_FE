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
    object Unauthorized : ProfileState()
}

class ProfileViewModel : BaseViewModel() {
    
    private val repository = ProfileRepository()
    
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState
    
    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            try {
                android.util.Log.d("ProfileViewModel", "Loading profile data...")
                val profileResult = repository.getProfile().getOrThrow()
                android.util.Log.d("ProfileViewModel", "Profile result: success=${profileResult.success}")

                val watchlistResult = repository.getWatchlist().getOrThrow()
                android.util.Log.d("ProfileViewModel", "Watchlist loaded: ${watchlistResult.watchlist.size} items")

                val favoritesResult = repository.getFavorites().getOrThrow()
                android.util.Log.d("ProfileViewModel", "Favorites loaded: ${favoritesResult.favorites.size} items")

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
                    android.util.Log.d("ProfileViewModel", "Profile state set to Success")
                } else {
                    android.util.Log.e("ProfileViewModel", "Profile load failed: ${profileResult.message}")
                    _profileState.value = ProfileState.Error(profileResult.message)
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Error loading profile", e)

                // Check if it's an authentication error
                if (e is retrofit2.HttpException && e.code() == 401) {
                    android.util.Log.w("ProfileViewModel", "401 Unauthorized - token invalid or expired")
                    _profileState.value = ProfileState.Unauthorized
                } else {
                    val errorMessage = e.message ?: "Failed to load profile"
                    _profileState.value = ProfileState.Error(errorMessage)
                }
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
