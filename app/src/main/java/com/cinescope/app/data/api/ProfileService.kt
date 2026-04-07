package com.cinescope.app.data.api

import com.cinescope.app.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileService {
    
    @GET("api/profile")
    suspend fun getProfile(): ApiResponse<ProfileData>
    
    @PUT("api/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): ApiResponse<ProfileData>
    
    @GET("api/profile/watchlist")
    suspend fun getWatchlist(): ApiResponse<WatchlistResponse>
    
    @PUT("api/profile/watchlist")
    suspend fun updateWatchlist(
        @Body request: UpdateListRequest
    ): ApiResponse<Any>
    
    @GET("api/profile/favorites")
    suspend fun getFavorites(): ApiResponse<FavoritesResponse>
    
    @PUT("api/profile/favorites")
    suspend fun updateFavorites(
        @Body request: UpdateListRequest
    ): ApiResponse<Any>
    
    @GET("api/profile/status/{imdbId}")
    suspend fun checkMovieStatus(
        @Path("imdbId") imdbId: String
    ): ApiResponse<MovieStatusResponse>
}

data class UpdateProfileRequest(
    val name: String
)
