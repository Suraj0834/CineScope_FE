package com.cinescope.app.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

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
}
