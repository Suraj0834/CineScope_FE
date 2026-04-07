package com.cinescope.app.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseViewModel : ViewModel() {

    protected suspend fun <T> launchCatching(
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit,
        block: suspend () -> T
    ) {
        try {
            val result = block()
            onSuccess(result)
        } catch (e: Exception) {
            onError(e)
        }
    }

    /**
     * Convert exception to user-friendly error message
     */
    protected fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> "No internet connection. Please check your network."
            is ConnectException -> "Cannot connect to server. Please try again later."
            is SocketTimeoutException -> "Connection timeout. Please check your internet speed."
            is IOException -> "Network error. Please check your connection."
            else -> throwable.message ?: "An unexpected error occurred"
        }
    }
}
