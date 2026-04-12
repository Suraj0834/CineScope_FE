package com.mrnoone.cinescope.ui.auth

import androidx.lifecycle.viewModelScope
import com.mrnoone.cinescope.data.model.User
import com.mrnoone.cinescope.data.repository.AuthRepository
import com.mrnoone.cinescope.ui.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel() {
    
    private val repository = AuthRepository()
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    
    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        
        viewModelScope.launch {
            repository.login(email, password)
                .onSuccess { response ->
                    if (response.success && response.data != null) {
                        _loginState.value = LoginState.Success(
                            response.data.token,
                            response.data.user
                        )
                    } else {
                        _loginState.value = LoginState.Error(response.message)
                    }
                }
                .onFailure { error ->
                    _loginState.value = LoginState.Error(
                        error.message ?: "Login failed. Please try again."
                    )
                }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String, val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
