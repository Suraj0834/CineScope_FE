package com.cinescope.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinescope.app.data.model.*
import com.cinescope.app.data.repository.AiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    
    private val repository = AiRepository()
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private var movieMetadata: MovieChatMetadata? = null
    
    fun setMovieMetadata(metadata: MovieChatMetadata) {
        this.movieMetadata = metadata
        
        // Add welcome message
        val welcomeMessage = ChatMessage(
            message = "Hi! I'm your AI movie assistant. Ask me anything about \"${metadata.title}\" - rumors, myths, trivia, behind-the-scenes stories, or any confusing plot points!",
            role = "assistant"
        )
        _messages.value = listOf(welcomeMessage)
    }
    
    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank() || movieMetadata == null) return
        
        // Add user message
        val newUserMessage = ChatMessage(
            message = userMessage.trim(),
            role = "user"
        )
        _messages.value = _messages.value + newUserMessage
        
        // Get AI response
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            // Add typing indicator message
            val typingMessage = ChatMessage(
                message = "",
                role = "assistant",
                isTyping = true
            )
            _messages.value = _messages.value + typingMessage

            try {
                // Build conversation history
                val history = _messages.value.dropLast(1).map { msg ->
                    ConversationMessage(
                        role = if (msg.role == "assistant") "model" else msg.role,
                        message = msg.message
                    )
                }
                
                val request = ChatRequest(
                    movieMetadata = movieMetadata!!,
                    userMessage = userMessage.trim(),
                    conversationHistory = if (history.isNotEmpty()) history else null
                )
                
                val response = repository.chat(request)
                
                if (response.success && response.data != null) {
                    val recs = response.data.recommendations
                    val aiMessage = ChatMessage(
                        message = response.data.response,
                        role = "assistant",
                        recommendations = recs
                    )
                    // Remove typing message and append actual AI message
                    _messages.value = _messages.value.filter { !it.isTyping } + aiMessage
                } else {
                    // Remove typing indicator
                    _messages.value = _messages.value.filter { !it.isTyping }
                    _error.value = response.message ?: "Failed to get response"
                }
            } catch (e: Exception) {
                // Remove typing indicator
                _messages.value = _messages.value.filter { !it.isTyping }
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
