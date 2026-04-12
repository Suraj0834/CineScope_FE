package com.mrnoone.cinescope.data.repository

import com.mrnoone.cinescope.data.api.ApiClient
import com.mrnoone.cinescope.data.api.UpdateProfileRequest
import com.mrnoone.cinescope.data.model.*

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
            android.util.Log.d("ProfileRepository", "addToWatchlist: imdbId=$imdbId, title=$title")
            val request = AddToListRequest(imdbId, title, posterPath)
            val response = profileService.addToWatchlist(request)
            android.util.Log.d("ProfileRepository", "addToWatchlist: response.success=${response.success}, message=${response.message}")
            Result.success(response)
        } catch (e: Exception) {
            android.util.Log.e("ProfileRepository", "addToWatchlist: Error", e)
            Result.failure(e)
        }
    }

    /**
     * Remove movie from watchlist
     */
    suspend fun removeFromWatchlist(imdbId: String): Result<ApiResponse<Any>> {
        return try {
            android.util.Log.d("ProfileRepository", "removeFromWatchlist: imdbId=$imdbId")
            val response = profileService.removeFromWatchlist(imdbId)
            android.util.Log.d("ProfileRepository", "removeFromWatchlist: response.success=${response.success}")
            Result.success(response)
        } catch (e: Exception) {
            android.util.Log.e("ProfileRepository", "removeFromWatchlist: Error", e)
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
            android.util.Log.d("ProfileRepository", "addToFavorites: imdbId=$imdbId, title=$title")
            val request = AddToListRequest(imdbId, title, posterPath)
            val response = profileService.addToFavorites(request)
            android.util.Log.d("ProfileRepository", "addToFavorites: response.success=${response.success}, message=${response.message}")
            Result.success(response)
        } catch (e: Exception) {
            android.util.Log.e("ProfileRepository", "addToFavorites: Error", e)
            Result.failure(e)
        }
    }

    /**
     * Remove movie from favorites
     */
    suspend fun removeFromFavorites(imdbId: String): Result<ApiResponse<Any>> {
        return try {
            android.util.Log.d("ProfileRepository", "removeFromFavorites: imdbId=$imdbId")
            val response = profileService.removeFromFavorites(imdbId)
            android.util.Log.d("ProfileRepository", "removeFromFavorites: response.success=${response.success}")
            Result.success(response)
        } catch (e: Exception) {
            android.util.Log.e("ProfileRepository", "removeFromFavorites: Error", e)
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
