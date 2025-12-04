package com.cinescope.app.data.model

import com.google.gson.annotations.SerializedName

data class WatchmodeSource(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("region")
    val region: String?,
    
    @SerializedName("webUrl")
    val webUrl: String?,
    
    @SerializedName("format")
    val format: String?,
    
    @SerializedName("price")
    val price: Double?
)

data class AiSummaryResponse(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("summary")
    val summary: String
)

data class ProfileData(
    @SerializedName("user")
    val user: User,
    
    @SerializedName("watchlist")
    val watchlist: List<WatchlistItem>? = null,
    
    @SerializedName("favorites")
    val favorites: List<WatchlistItem>? = null
)
