package com.mrnoone.cinescope.data.api

import com.mrnoone.cinescope.data.model.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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

    @POST("api/profile/watchlist")
    suspend fun addToWatchlist(
        @Body request: AddToListRequest
    ): ApiResponse<Any>

    @DELETE("api/profile/watchlist/{imdbId}")
    suspend fun removeFromWatchlist(
        @Path("imdbId") imdbId: String
    ): ApiResponse<Any>

    @GET("api/profile/favorites")
    suspend fun getFavorites(): ApiResponse<FavoritesResponse>

    @POST("api/profile/favorites")
    suspend fun addToFavorites(
        @Body request: AddToListRequest
    ): ApiResponse<Any>

    @DELETE("api/profile/favorites/{imdbId}")
    suspend fun removeFromFavorites(
        @Path("imdbId") imdbId: String
    ): ApiResponse<Any>
    
    @GET("api/profile/status/{imdbId}")
    suspend fun checkMovieStatus(
        @Path("imdbId") imdbId: String
    ): ApiResponse<MovieStatusResponse>
}

data class UpdateProfileRequest(
    val name: String
)
