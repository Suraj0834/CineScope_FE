package com.mrnoone.cinescope.ui.auth

import androidx.lifecycle.viewModelScope
import com.mrnoone.cinescope.data.repository.AuthRepository
import com.mrnoone.cinescope.ui.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResetPasswordViewModel : BaseViewModel() {
    
    private val repository = AuthRepository()
    
    private val _resetPasswordState = MutableStateFlow<ResetPasswordState>(ResetPasswordState.Idle)
    val resetPasswordState: StateFlow<ResetPasswordState> = _resetPasswordState
    
    fun resetPassword(email: String, otp: String, newPassword: String) {
        _resetPasswordState.value = ResetPasswordState.Loading
        
        viewModelScope.launch {
            repository.verifyOtp(email, otp, newPassword)
                .onSuccess { response ->
                    if (response.success) {
                        _resetPasswordState.value = ResetPasswordState.Success(response.message)
                    } else {
                        _resetPasswordState.value = ResetPasswordState.Error(response.message)
                    }
                }
                .onFailure { error ->
                    _resetPasswordState.value = ResetPasswordState.Error(
                        error.message ?: "Failed to reset password. Please try again."
                    )
                }
        }
    }
}

sealed class ResetPasswordState {
    object Idle : ResetPasswordState()
    object Loading : ResetPasswordState()
    data class Success(val message: String) : ResetPasswordState()
    data class Error(val message: String) : ResetPasswordState()
}
