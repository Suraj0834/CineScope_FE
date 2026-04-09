package com.cinescope.app.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("watchlistCount")
    val watchlistCount: Int? = 0,
    
    @SerializedName("favoritesCount")
    val favoritesCount: Int? = 0,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class WatchlistItem(
    @SerializedName("imdbId")
    val imdbId: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("posterPath")
    val posterPath: String?,

    @SerializedName("addedAt")
    val addedAt: String
)

data class FavoriteItem(
    @SerializedName("imdbId")
    val imdbId: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("posterPath")
    val posterPath: String?,

    @SerializedName("addedAt")
    val addedAt: String
)

data class WatchlistResponse(
    @SerializedName("watchlist")
    val watchlist: List<WatchlistItem>,
    
    @SerializedName("count")
    val count: Int
)

data class FavoritesResponse(
    @SerializedName("favorites")
    val favorites: List<FavoriteItem>,
    
    @SerializedName("count")
    val count: Int
)

data class MovieStatusResponse(
    @SerializedName("imdbId")
    val imdbId: String,

    @SerializedName("isInWatchlist")
    val isInWatchlist: Boolean,

    @SerializedName("isInFavorites")
    val isInFavorites: Boolean
)

// Request for POST endpoints (add to list) - no action field needed
data class AddToListRequest(
    @SerializedName("imdbId")
    val imdbId: String,

    @SerializedName("title")
    val title: String?,

    @SerializedName("posterPath")
    val posterPath: String?
)

// Legacy request for PUT endpoints (kept for backward compatibility)
data class UpdateListRequest(
    @SerializedName("imdbId")
    val imdbId: String,

    @SerializedName("action")
    val action: String, // "add" or "remove"

    @SerializedName("title")
    val title: String?,

    @SerializedName("posterPath")
    val posterPath: String?
)
