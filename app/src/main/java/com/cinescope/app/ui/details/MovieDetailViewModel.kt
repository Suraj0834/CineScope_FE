package com.cinescope.app.ui.details

import androidx.lifecycle.viewModelScope
import com.cinescope.app.data.model.*
import com.cinescope.app.data.repository.AiRepository
import com.cinescope.app.data.repository.MoviesRepository
import com.cinescope.app.data.repository.ProfileRepository
import com.cinescope.app.ui.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MovieDetailState {
    object Idle : MovieDetailState()
    object Loading : MovieDetailState()
    data class Success(val movieDetail: MovieDetail) : MovieDetailState()
    data class Error(val message: String) : MovieDetailState()
}

sealed class SummaryState {
    object Idle : SummaryState()
    object Loading : SummaryState()
    data class Success(val summary: String) : SummaryState()
    data class Error(val message: String) : SummaryState()
}

sealed class MovieStatusState {
    object Idle : MovieStatusState()
    object Loading : MovieStatusState()
    data class Success(val isInWatchlist: Boolean, val isInFavorites: Boolean) : MovieStatusState()
    data class Error(val message: String) : MovieStatusState()
}

class MovieDetailViewModel : BaseViewModel() {
    
    private val moviesRepository = MoviesRepository()
    private val aiRepository = AiRepository()
    private val profileRepository = ProfileRepository()
    
    private val _detailState = MutableStateFlow<MovieDetailState>(MovieDetailState.Idle)
    val detailState: StateFlow<MovieDetailState> = _detailState
    
    private val _summaryState = MutableStateFlow<SummaryState>(SummaryState.Idle)
    val summaryState: StateFlow<SummaryState> = _summaryState
    
    private val _statusState = MutableStateFlow<MovieStatusState>(MovieStatusState.Idle)
    val statusState: StateFlow<MovieStatusState> = _statusState
    
    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage
    
    private var currentMovieDetail: MovieDetail? = null
    private var currentImdbId: String? = null
    
    /**
     * Load movie details by IMDB ID
     */
    fun loadMovieDetails(imdbId: String) {
        currentImdbId = imdbId
        android.util.Log.d("MovieDetailViewModel", "loadMovieDetails called with imdbId: $imdbId")
        viewModelScope.launch {
            _detailState.value = MovieDetailState.Loading
            android.util.Log.d("MovieDetailViewModel", "State set to Loading")

            launchCatching(
                onSuccess = { result ->
                    android.util.Log.d("MovieDetailViewModel", "API call successful: ${result.success}, has data: ${result.data?.movie != null}")
                    if (result.success && result.data?.movie != null) {
                        currentMovieDetail = result.data.movie
                        android.util.Log.d("MovieDetailViewModel", "Setting state to Success with movie: ${result.data.movie.title}")
                        _detailState.value = MovieDetailState.Success(result.data.movie)
                        android.util.Log.d("MovieDetailViewModel", "State is now: ${_detailState.value}")
                        // Also check movie status
                        checkMovieStatus(imdbId)
                    } else {
                        android.util.Log.e("MovieDetailViewModel", "API call failed or no data: ${result.message}")
                        _detailState.value = MovieDetailState.Error(result.message ?: "Failed to load movie details")
                    }
                },
                onError = { error ->
                    android.util.Log.e("MovieDetailViewModel", "Error loading movie details", error)
                    _detailState.value = MovieDetailState.Error(getErrorMessage(error))
                }
            ) {
                android.util.Log.d("MovieDetailViewModel", "Making API call to getMovieDetail")
                val response = moviesRepository.getMovieDetail(imdbId)
                android.util.Log.d("MovieDetailViewModel", "API response received")
                response.getOrThrow()
            }
        }
    }
    
    /**
     * Check if movie is in watchlist or favorites
     */
    fun checkMovieStatus(imdbId: String) {
        viewModelScope.launch {
            _statusState.value = MovieStatusState.Loading
            
            launchCatching(
                onSuccess = { result ->
                    _statusState.value = MovieStatusState.Success(
                        result.isInWatchlist,
                        result.isInFavorites
                    )
                },
                onError = { error ->
                    _statusState.value = MovieStatusState.Error(getErrorMessage(error))
                }
            ) {
                profileRepository.checkMovieStatus(imdbId).getOrThrow()
            }
        }
    }
    
    /**
     * Get AI summary for the movie
     */
    fun getAiSummary() {
        val movieDetail = currentMovieDetail ?: return
        
        viewModelScope.launch {
            _summaryState.value = SummaryState.Loading
            
            launchCatching(
                onSuccess = { result ->
                    _summaryState.value = SummaryState.Success(result.summary)
                },
                onError = { error ->
                    _summaryState.value = SummaryState.Error(getErrorMessage(error))
                }
            ) {
                aiRepository.getSummary(
                    movieDetail.title,
                    movieDetail.overview ?: ""
                )
            }
        }
    }
    
    /**
     * Add movie to watchlist
     */
    fun addToWatchlist() {
        val imdbId = currentImdbId ?: return
        val movieDetail = currentMovieDetail ?: return
        
        viewModelScope.launch {
            launchCatching(
                onSuccess = {
                    _actionMessage.value = "Added to watchlist"
                    // Refresh status
                    checkMovieStatus(imdbId)
                },
                onError = { error ->
                    _actionMessage.value = getErrorMessage(error)
                }
            ) {
                profileRepository.addToWatchlist(
                    imdbId,
                    movieDetail.title,
                    movieDetail.posterPath
                )
            }
        }
    }
    
    /**
     * Remove movie from watchlist
     */
    fun removeFromWatchlist() {
        val imdbId = currentImdbId ?: return
        
        viewModelScope.launch {
            launchCatching(
                onSuccess = {
                    _actionMessage.value = "Removed from watchlist"
                    // Refresh status
                    checkMovieStatus(imdbId)
                },
                onError = { error ->
                    _actionMessage.value = getErrorMessage(error)
                }
            ) {
                profileRepository.removeFromWatchlist(imdbId)
            }
        }
    }
    
    /**
     * Add movie to favorites
     */
    fun addToFavorites() {
        val imdbId = currentImdbId ?: return
        val movieDetail = currentMovieDetail ?: return
        
        viewModelScope.launch {
            launchCatching(
                onSuccess = {
                    _actionMessage.value = "Added to favorites"
                    // Refresh status
                    checkMovieStatus(imdbId)
                },
                onError = { error ->
                    _actionMessage.value = getErrorMessage(error)
                }
            ) {
                profileRepository.addToFavorites(
                    imdbId,
                    movieDetail.title,
                    movieDetail.posterPath
                )
            }
        }
    }
    
    /**
     * Remove movie from favorites
     */
    fun removeFromFavorites() {
        val imdbId = currentImdbId ?: return
        
        viewModelScope.launch {
            launchCatching(
                onSuccess = {
                    _actionMessage.value = "Removed from favorites"
                    // Refresh status
                    checkMovieStatus(imdbId)
                },
                onError = { error ->
                    _actionMessage.value = getErrorMessage(error)
                }
            ) {
                profileRepository.removeFromFavorites(imdbId)
            }
        }
    }
    
    /**
     * Clear action message
     */
    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
