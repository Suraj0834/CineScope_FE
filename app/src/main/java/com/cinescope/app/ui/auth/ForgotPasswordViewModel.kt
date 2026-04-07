package com.cinescope.app.ui.auth

import androidx.lifecycle.viewModelScope
import com.cinescope.app.data.repository.AuthRepository
import com.cinescope.app.ui.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : BaseViewModel() {

    private val repository = AuthRepository()

    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState

    // Track rate limiting
    private var lastRequestTime: Long = 0
    private val rateLimitMs: Long = 30000 // 30 seconds

    /**
     * Send OTP to user's email
     */
    fun sendOtp(email: String) {
        // Check rate limiting
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRequestTime < rateLimitMs) {
            val remainingSeconds = (rateLimitMs - (currentTime - lastRequestTime)) / 1000
            _forgotPasswordState.value = ForgotPasswordState.Error(
                "Please wait $remainingSeconds seconds before requesting again"
            )
            return
        }

        _forgotPasswordState.value = ForgotPasswordState.Loading

        viewModelScope.launch {
            // Simulate minimum loading time for better UX
            val startTime = System.currentTimeMillis()

            repository.forgotPassword(email)
                .onSuccess { response ->
                    // Ensure minimum loading time of 1 second
                    val elapsedTime = System.currentTimeMillis() - startTime
                    if (elapsedTime < 1000) {
                        delay(1000 - elapsedTime)
                    }

                    if (response.success) {
                        lastRequestTime = System.currentTimeMillis()
                        _forgotPasswordState.value = ForgotPasswordState.Success(
                            response.message.ifEmpty {
                                "Password reset link has been sent to your email"
                            }
                        )
                    } else {
                        _forgotPasswordState.value = ForgotPasswordState.Error(
                            response.message.ifEmpty {
                                "Failed to send reset link. Please try again."
                            }
                        )
                    }
                }
                .onFailure { error ->
                    // Ensure minimum loading time of 1 second
                    val elapsedTime = System.currentTimeMillis() - startTime
                    if (elapsedTime < 1000) {
                        delay(1000 - elapsedTime)
                    }

                    _forgotPasswordState.value = ForgotPasswordState.Error(
                        when {
                            error.message?.contains("network", ignoreCase = true) == true ->
                                "Network error. Please check your connection."
                            error.message?.contains("timeout", ignoreCase = true) == true ->
                                "Request timed out. Please try again."
                            error.message?.contains("not found", ignoreCase = true) == true ->
                                "Email not found. Please check and try again."
                            else ->
                                error.message ?: "Failed to send reset link. Please try again."
                        }
                    )
                }
        }
    }

    /**
     * Reset state to idle
     */
    fun resetState() {
        _forgotPasswordState.value = ForgotPasswordState.Idle
    }

    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    data class Success(val message: String) : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}
