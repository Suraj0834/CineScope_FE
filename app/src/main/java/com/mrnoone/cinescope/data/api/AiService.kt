package com.mrnoone.cinescope.data.api

import com.mrnoone.cinescope.data.model.AiSummaryResponse
import com.mrnoone.cinescope.data.model.ApiResponse
import com.mrnoone.cinescope.data.model.ChatRequest
import com.mrnoone.cinescope.data.model.ChatResponseWithRecommendations
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
