package com.cinescope.app.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("imdbId")
    val imdbId: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("originalTitle")
    val originalTitle: String? = null,
    
    @SerializedName("overview")
    val overview: String? = null,
    
    @SerializedName("releaseDate")
    val releaseDate: String? = null,
    
    @SerializedName("posterPath")
    val posterPath: String?,
    
    @SerializedName("backdropPath")
    val backdropPath: String? = null,
    
    @SerializedName("voteAverage")
    val voteAverage: Double? = null,
    
    @SerializedName("voteCount")
    val voteCount: Int? = null,
    
    @SerializedName("popularity")
    val popularity: Double? = null,
    
    @SerializedName("adult")
    val adult: Boolean? = false,
    
    @SerializedName("genreIds")
    val genreIds: List<Int>? = null,
    
    // Backward compatibility
    @SerializedName("tmdbId")
    val tmdbId: Int? = null,
    
    // Trakt ID for movie details
    @SerializedName("traktId")
    val traktId: Int? = null,
    
    // Legacy fields for compatibility
    @SerializedName("year")
    val year: String? = null,
    
    @SerializedName("poster")
    val poster: String? = null,
    
    @SerializedName("type")
    val type: String? = "movie",
    
    @SerializedName("imdbRating")
    val imdbRating: Double? = null,
    
    @SerializedName("genre")
    val genre: String? = null
) {
    // Helper property to get year from releaseDate
    val displayYear: String
        get() = year ?: releaseDate?.take(4) ?: ""
    
    // Helper property to get poster URL (handles both old and new format)
    val displayPoster: String?
        get() = posterPath ?: poster
}

data class MoviesResponse(
    @SerializedName("query")
    val query: String? = null,
    
    @SerializedName("page")
    val page: Int = 1,
    
    @SerializedName("totalPages")
    val totalPages: Int = 0,
    
    @SerializedName("totalResults")
    val totalResults: Int = 0,
    
    @SerializedName("hasMore")
    val hasMore: Boolean = true,
    
    @SerializedName("movies")
    val movies: List<Movie>
)

data class Genre(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String
)

data class GenresResponse(
    @SerializedName("genres")
    val genres: List<Genre>
)
