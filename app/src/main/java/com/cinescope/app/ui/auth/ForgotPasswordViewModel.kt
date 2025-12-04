package com.cinescope.app.ui.auth

import androidx.lifecycle.viewModelScope
import com.cinescope.app.data.repository.AuthRepository
import com.cinescope.app.ui.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : BaseViewModel() {
    
    private val repository = AuthRepository()
    
    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState
    
    fun sendOtp(email: String) {
        _forgotPasswordState.value = ForgotPasswordState.Loading
        
        viewModelScope.launch {
            repository.forgotPassword(email)
                .onSuccess { response ->
                    if (response.success) {
                        _forgotPasswordState.value = ForgotPasswordState.Success(response.message)
                    } else {
                        _forgotPasswordState.value = ForgotPasswordState.Error(response.message)
                    }
                }
                .onFailure { error ->
                    _forgotPasswordState.value = ForgotPasswordState.Error(
                        error.message ?: "Failed to send OTP. Please try again."
                    )
                }
        }
    }
}

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    data class Success(val message: String) : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}
