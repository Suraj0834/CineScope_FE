package com.cinescope.app.ui.person

import androidx.lifecycle.viewModelScope
import com.cinescope.app.data.model.*
import com.cinescope.app.data.repository.PersonRepository
import com.cinescope.app.ui.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PersonDetailState {
    object Idle : PersonDetailState()
    object Loading : PersonDetailState()
    data class Success(val person: Person) : PersonDetailState()
    data class Error(val message: String) : PersonDetailState()
}

sealed class FilmographyState {
    object Idle : FilmographyState()
    object Loading : FilmographyState()
    data class Success(val filmography: Filmography) : FilmographyState()
    data class Error(val message: String) : FilmographyState()
}

sealed class PersonImagesState {
    object Idle : PersonImagesState()
    object Loading : PersonImagesState()
    data class Success(val images: PersonImages) : PersonImagesState()
    data class Error(val message: String) : PersonImagesState()
}

class PersonDetailViewModel : BaseViewModel() {
    
    private val repository = PersonRepository()
    
    private val _personState = MutableStateFlow<PersonDetailState>(PersonDetailState.Idle)
    val personState: StateFlow<PersonDetailState> = _personState
    
    private val _filmographyState = MutableStateFlow<FilmographyState>(FilmographyState.Idle)
    val filmographyState: StateFlow<FilmographyState> = _filmographyState
    
    private val _imagesState = MutableStateFlow<PersonImagesState>(PersonImagesState.Idle)
    val imagesState: StateFlow<PersonImagesState> = _imagesState
    
    /**
     * Load person details
     */
    fun loadPersonDetails(personId: Int) {
        viewModelScope.launch {
            _personState.value = PersonDetailState.Loading
            
            launchCatching(
                onSuccess = { result ->
                    if (result.success && result.data != null) {
                        _personState.value = PersonDetailState.Success(result.data)
                    } else {
                        _personState.value = PersonDetailState.Error(result.message ?: "Failed to load person details")
                    }
                },
                onError = { error ->
                    _personState.value = PersonDetailState.Error(error.message ?: "Failed to load person details")
                }
            ) {
                val response = repository.getPersonDetail(personId)
                response.getOrThrow()
            }
        }
    }
    
    /**
     * Load person's filmography
     */
    fun loadFilmography(personId: Int) {
        viewModelScope.launch {
            _filmographyState.value = FilmographyState.Loading
            
            launchCatching(
                onSuccess = { result ->
                    if (result.success && result.data != null) {
                        _filmographyState.value = FilmographyState.Success(result.data)
                    } else {
                        _filmographyState.value = FilmographyState.Error(result.message ?: "Failed to load filmography")
                    }
                },
                onError = { error ->
                    _filmographyState.value = FilmographyState.Error(error.message ?: "Failed to load filmography")
                }
            ) {
                val response = repository.getPersonFilmography(personId)
                response.getOrThrow()
            }
        }
    }
    
    /**
     * Load person's images
     */
    fun loadImages(personId: Int) {
        viewModelScope.launch {
            _imagesState.value = PersonImagesState.Loading
            
            launchCatching(
                onSuccess = { result ->
                    if (result.success && result.data != null) {
                        _imagesState.value = PersonImagesState.Success(result.data)
                    } else {
                        _imagesState.value = PersonImagesState.Error(result.message ?: "Failed to load images")
                    }
                },
                onError = { error ->
                    _imagesState.value = PersonImagesState.Error(error.message ?: "Failed to load images")
                }
            ) {
                val response = repository.getPersonImages(personId)
                response.getOrThrow()
            }
        }
    }
    
    /**
     * Load all person data at once
     */
    fun loadAllPersonData(personId: Int) {
        loadPersonDetails(personId)
        loadFilmography(personId)
        loadImages(personId)
    }
}
