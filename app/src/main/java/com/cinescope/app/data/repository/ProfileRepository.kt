package com.cinescope.app.data.repository

import com.cinescope.app.data.api.ApiClient
import com.cinescope.app.data.api.UpdateProfileRequest
import com.cinescope.app.data.model.*

class ProfileRepository {
    
    private val profileService = ApiClient.profileService
    
    /**
     * Get user profile
     */
    suspend fun getProfile(): Result<ApiResponse<ProfileData>> {
        return try {
            val response = profileService.getProfile()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update user profile
     */
    suspend fun updateProfile(name: String): Result<ApiResponse<ProfileData>> {
        return try {
            val request = UpdateProfileRequest(name)
            val response = profileService.updateProfile(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get user's watchlist
     */
    suspend fun getWatchlist(): Result<WatchlistResponse> {
        return try {
            val response = profileService.getWatchlist()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add movie to watchlist
     */
    suspend fun addToWatchlist(imdbId: String, title: String, posterPath: String?): Result<ApiResponse<Any>> {
        return try {
            val request = UpdateListRequest(imdbId, "add", title, posterPath)
            val response = profileService.updateWatchlist(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Remove movie from watchlist
     */
    suspend fun removeFromWatchlist(imdbId: String): Result<ApiResponse<Any>> {
        return try {
            val request = UpdateListRequest(imdbId, "remove", null, null)
            val response = profileService.updateWatchlist(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get user's favorites
     */
    suspend fun getFavorites(): Result<FavoritesResponse> {
        return try {
            val response = profileService.getFavorites()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add movie to favorites
     */
    suspend fun addToFavorites(imdbId: String, title: String, posterPath: String?): Result<ApiResponse<Any>> {
        return try {
            val request = UpdateListRequest(imdbId, "add", title, posterPath)
            val response = profileService.updateFavorites(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Remove movie from favorites
     */
    suspend fun removeFromFavorites(imdbId: String): Result<ApiResponse<Any>> {
        return try {
            val request = UpdateListRequest(imdbId, "remove", null, null)
            val response = profileService.updateFavorites(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if movie is in watchlist or favorites
     */
    suspend fun checkMovieStatus(imdbId: String): Result<MovieStatusResponse> {
        return try {
            val response = profileService.checkMovieStatus(imdbId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
