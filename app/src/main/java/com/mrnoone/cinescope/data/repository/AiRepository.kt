package com.mrnoone.cinescope.data.repository

import com.mrnoone.cinescope.data.api.ApiClient
import com.mrnoone.cinescope.data.api.SummarizeRequest
import com.mrnoone.cinescope.data.model.ChatRequest
import com.mrnoone.cinescope.data.model.ChatResponseWithRecommendations
import com.mrnoone.cinescope.data.model.ApiResponse
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