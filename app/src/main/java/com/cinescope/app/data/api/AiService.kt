package com.cinescope.app.data.api

import com.cinescope.app.data.model.AiSummaryResponse
import com.cinescope.app.data.model.ApiResponse
import com.cinescope.app.data.model.ChatRequest
import com.cinescope.app.data.model.ChatResponseWithRecommendations
import retrofit2.http.Body
import retrofit2.http.POST

interface AiService {
    
    @POST("api/gemini/summarize")
    suspend fun getSummary(
        @Body request: SummarizeRequest
    ): ApiResponse<AiSummaryResponse>
    
    @POST("api/gemini/chat")
    suspend fun chat(
        @Body request: ChatRequest
    ): ApiResponse<ChatResponseWithRecommendations>
}

data class SummarizeRequest(
    val title: String,
    val plot: String?,
    val year: String? = null,
    val genres: String? = null
)
