package com.mrnoone.cinescope.data.api

import com.mrnoone.cinescope.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

interface MoviesService {
    
    @GET("api/movies/trending")
    suspend fun getTrending(
        @Query("timeWindow") timeWindow: String = "day", // "day" or "week"
        @Query("page") page: Int = 1
    ): ApiResponse<MoviesResponse>
    
    @GET("api/movies/popular")
    suspend fun getPopular(
        @Query("page") page: Int = 1
    ): ApiResponse<MoviesResponse>
    
    @GET("api/movies/top-rated")
    suspend fun getTopRated(
        @Query("page") page: Int = 1
    ): ApiResponse<MoviesResponse>
    
    @GET("api/movies/search")
    suspend fun searchMovies(
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): ApiResponse<MoviesResponse>
    
    @GET("api/movies/genres")
    suspend fun getGenres(): ApiResponse<GenresResponse>
    
    @GET("api/movies/discover")
    suspend fun discoverMovies(
        @Query("page") page: Int = 1,
        @Query("genre") genre: String? = null,
        @Query("year") year: Int? = null,
        @Query("sortBy") sortBy: String = "popularity.desc",
        @Query("voteAverageGte") voteAverageGte: Double? = null,
        @Query("voteAverageLte") voteAverageLte: Double? = null
    ): ApiResponse<MoviesResponse>
    
    @GET("api/movies/{imdbId}")
    suspend fun getMovieDetail(
        @Path("imdbId") imdbId: String
    ): ApiResponse<MovieDetail>
    
    @GET("api/movies/{imdbId}/credits")
    suspend fun getMovieCredits(
        @Path("imdbId") imdbId: String
    ): ApiResponse<Credits>
    
    @GET("api/movies/{imdbId}/videos")
    suspend fun getMovieVideos(
        @Path("imdbId") imdbId: String
    ): ApiResponse<VideosResponse>
    
    @GET("api/movies/{imdbId}/watch-providers")
    suspend fun getWatchProviders(
        @Path("imdbId") imdbId: String,
        @Query("country") country: String = "IN"
    ): ApiResponse<WatchProvidersResponse>
}
