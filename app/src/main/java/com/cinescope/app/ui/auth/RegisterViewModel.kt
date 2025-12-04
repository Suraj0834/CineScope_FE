package com.cinescope.app.ui.auth

import androidx.lifecycle.viewModelScope
import com.cinescope.app.data.model.User
import com.cinescope.app.data.repository.AuthRepository
import com.cinescope.app.ui.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : BaseViewModel() {
    
    private val repository = AuthRepository()
    
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState
    
    fun register(name: String, email: String, password: String) {
        _registerState.value = RegisterState.Loading
        
        viewModelScope.launch {
            repository.register(name, email, password)
                .onSuccess { response ->
                    if (response.success && response.data != null) {
                        _registerState.value = RegisterState.Success(
                            response.data.token,
                            response.data.user
                        )
                    } else {
                        _registerState.value = RegisterState.Error(response.message)
                    }
                }
                .onFailure { error ->
                    _registerState.value = RegisterState.Error(
                        error.message ?: "Registration failed. Please try again."
                    )
                }
        }
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val token: String, val user: User) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
