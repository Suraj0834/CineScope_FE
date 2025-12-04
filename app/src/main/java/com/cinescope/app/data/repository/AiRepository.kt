package com.cinescope.app.data.repository

import com.cinescope.app.data.api.ApiClient
import com.cinescope.app.data.api.SummarizeRequest
import com.cinescope.app.data.model.ChatRequest
import com.cinescope.app.data.model.ChatResponseWithRecommendations
import com.cinescope.app.data.model.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AiResponse(
    val summary: String,
    val recommendations: List<String>? = null
)

class AiRepository {
    
    private val aiService = ApiClient.aiService
    
    suspend fun getSummary(title: String, plot: String): AiResponse {
        return withContext(Dispatchers.IO) {
            val request = SummarizeRequest(title = title, plot = plot)
            val response = aiService.getSummary(request)
            if (response.success && response.data != null) {
                AiResponse(summary = response.data.summary)
            } else {
                throw Exception(response.message)
            }
        }
    }
    
    suspend fun chat(chatRequest: ChatRequest): ApiResponse<ChatResponseWithRecommendations> {
        return withContext(Dispatchers.IO) {
            aiService.chat(chatRequest)
        }
    }
}