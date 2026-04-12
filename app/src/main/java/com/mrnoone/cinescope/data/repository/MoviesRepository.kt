package com.mrnoone.cinescope.data.repository

import com.mrnoone.cinescope.data.api.ApiClient
import com.mrnoone.cinescope.data.model.*

class MoviesRepository {
    
    private val moviesService = ApiClient.moviesService
    
    // Cache for frequently accessed data
    private val genresCache = mutableMapOf<Int, Genre>()
    
    /**
     * Get trending movies
     */
    suspend fun getTrending(timeWindow: String = "day", page: Int = 1): Result<ApiResponse<MoviesResponse>> {
        return try {
            val response = moviesService.getTrending(timeWindow, page)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get popular movies
     */
    suspend fun getPopular(page: Int = 1): Result<ApiResponse<MoviesResponse>> {
        return try {
            val response = moviesService.getPopular(page)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get top rated movies
     */
    suspend fun getTopRated(page: Int = 1): Result<ApiResponse<MoviesResponse>> {
        return try {
            val response = moviesService.getTopRated(page)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Search movies by title
     */
    suspend fun searchMovies(query: String, page: Int = 1): Result<ApiResponse<MoviesResponse>> {
        return try {
            val response = moviesService.searchMovies(query, page)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all movie genres
     */
    suspend fun getGenres(): Result<ApiResponse<GenresResponse>> {
        return try {
            val response = moviesService.getGenres()
            // Cache genres for later use
            response.data?.genres?.forEach { genre ->
                genresCache[genre.id] = genre
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Discover movies with filters
     */
    suspend fun discoverMovies(
        page: Int = 1,
        genre: String? = null,
        year: Int? = null,
        sortBy: String = "popularity.desc",
        voteAverageGte: Double? = null,
        voteAverageLte: Double? = null
    ): Result<ApiResponse<MoviesResponse>> {
        return try {
            val response = moviesService.discoverMovies(
                page, genre, year, sortBy, voteAverageGte, voteAverageLte
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get movie details by IMDB ID
     */
    suspend fun getMovieDetail(imdbId: String): Result<ApiResponse<MovieDetail>> {
        return try {
            android.util.Log.d("MoviesRepository", "getMovieDetail: Calling API for imdbId=$imdbId")
            val response = moviesService.getMovieDetail(imdbId)
            android.util.Log.d("MoviesRepository", "getMovieDetail: API response received: success=${response.success}, hasData=${response.data != null}")
            android.util.Log.d("MoviesRepository", "getMovieDetail: Movie title=${response.data?.title}")
            Result.success(response)
        } catch (e: Exception) {
            android.util.Log.e("MoviesRepository", "getMovieDetail: Exception caught", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get movie credits (cast and crew)
     */
    suspend fun getMovieCredits(imdbId: String): Result<ApiResponse<Credits>> {
        return try {
            val response = moviesService.getMovieCredits(imdbId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get movie videos (trailers, teasers)
     */
    suspend fun getMovieVideos(imdbId: String): Result<ApiResponse<VideosResponse>> {
        return try {
            val response = moviesService.getMovieVideos(imdbId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get watch providers (streaming availability)
     */
    suspend fun getWatchProviders(imdbId: String, country: String = "IN"): Result<ApiResponse<WatchProvidersResponse>> {
        return try {
            val response = moviesService.getWatchProviders(imdbId, country)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get genre name by ID from cache
     */
    fun getGenreName(genreId: Int): String? {
        return genresCache[genreId]?.name
    }
    
    /**
     * Get genre names for a list of IDs
     */
    fun getGenreNames(genreIds: List<Int>?): String {
        if (genreIds.isNullOrEmpty()) return ""
        return genreIds.mapNotNull { getGenreName(it) }.joinToString(", ")
    }
}
