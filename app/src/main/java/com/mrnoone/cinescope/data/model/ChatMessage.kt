package com.mrnoone.cinescope.data.model

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val message: String,
    val role: String, // "user" or "assistant"
    val timestamp: Long = System.currentTimeMillis(),
    // Optional structured recommendations when role == "assistant"
    val recommendations: List<Recommendation>? = null
    ,
    // Typing indicator flag
    val isTyping: Boolean = false
)

data class Recommendation(
    @SerializedName("title")
    val title: String,

    @SerializedName("imdbId")
    val imdbId: String? = null,

    @SerializedName("poster")
    val poster: String? = null,

    @SerializedName("year")
    val year: String? = null,

    @SerializedName("reason")
    val reason: String? = null
)

data class ChatRequest(
    @SerializedName("movieMetadata")
    val movieMetadata: MovieChatMetadata,
    
    @SerializedName("userMessage")
    val userMessage: String,
    
    @SerializedName("conversationHistory")
    val conversationHistory: List<ConversationMessage>? = null
)

data class ConversationMessage(
    @SerializedName("role")
    val role: String,
    
    @SerializedName("message")
    val message: String
)

data class MovieChatMetadata(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("year")
    val year: String? = null,
    
    @SerializedName("rated")
    val rated: String? = null,
    
    @SerializedName("runtime")
    val runtime: String? = null,
    
    @SerializedName("genre")
    val genre: String? = null,
    
    @SerializedName("director")
    val director: String? = null,
    
    @SerializedName("writer")
    val writer: String? = null,
    
    @SerializedName("actors")
    val actors: String? = null,
    
    @SerializedName("plot")
    val plot: String? = null,
    
    @SerializedName("language")
    val language: String? = null,
    
    @SerializedName("country")
    val country: String? = null,
    
    @SerializedName("awards")
    val awards: String? = null,
    
    @SerializedName("imdbRating")
    val imdbRating: String? = null,
    
    @SerializedName("imdbVotes")
    val imdbVotes: String? = null,
    
    @SerializedName("boxOffice")
    val boxOffice: String? = null
)

data class ChatResponse(
    @SerializedName("response")
    val response: String,
    
    @SerializedName("movieTitle")
    val movieTitle: String
)

data class ChatResponseWithRecommendations(
    @SerializedName("response")
    val response: String,

    @SerializedName("movieTitle")
    val movieTitle: String,

    @SerializedName("recommendations")
    val recommendations: List<Recommendation>? = null
)
