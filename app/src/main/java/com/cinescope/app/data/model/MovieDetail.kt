package com.cinescope.app.data.model

import com.google.gson.annotations.SerializedName

data class MovieDetailResponse(
    @SerializedName("movie")
    val movie: MovieDetail
)

data class MovieDetail(
    @SerializedName("tmdbId")
    val tmdbId: Int,
    
    @SerializedName("imdbId")
    val imdbId: String?,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("originalTitle")
    val originalTitle: String?,
    
    @SerializedName("tagline")
    val tagline: String?,
    
    @SerializedName("overview")
    val overview: String?,
    
    @SerializedName("releaseDate")
    val releaseDate: String?,
    
    @SerializedName("runtime")
    val runtime: Int?,
    
    @SerializedName("status")
    val status: String?,
    
    @SerializedName("posterPath")
    val posterPath: String?,
    
    @SerializedName("backdropPath")
    val backdropPath: String?,
    
    @SerializedName("voteAverage")
    val voteAverage: Double?,
    
    @SerializedName("voteCount")
    val voteCount: Int?,
    
    @SerializedName("popularity")
    val popularity: Double?,
    
    @SerializedName("adult")
    val adult: Boolean?,
    
    @SerializedName("budget")
    val budget: Long?,
    
    @SerializedName("revenue")
    val revenue: Long?,
    
    @SerializedName("homepage")
    val homepage: String?,
    
    @SerializedName("genres")
    val genres: List<Genre>?,
    
    @SerializedName("productionCompanies")
    val productionCompanies: List<ProductionCompany>?,
    
    @SerializedName("productionCountries")
    val productionCountries: List<ProductionCountry>?,
    
    @SerializedName("spokenLanguages")
    val spokenLanguages: List<SpokenLanguage>?,
    
    @SerializedName("credits")
    val credits: Credits?,
    
    @SerializedName("videos")
    val videos: List<Video>?,
    
    @SerializedName("similar")
    val similar: List<Movie>?,
    
    @SerializedName("recommendations")
    val recommendations: List<Movie>?,
    
    @SerializedName("watchProviders")
    val watchProviders: Map<String, WatchProviderData>?,

    @SerializedName("watchmodeSources")
    val watchmodeSources: List<WatchmodeSource>?
) {
    // Helper properties
    val displayRuntime: String
        get() = runtime?.let { "${it / 60}h ${it % 60}m" } ?: "N/A"
    
    val displayYear: String
        get() = releaseDate?.take(4) ?: "N/A"
    
    val displayRating: String
        get() = voteAverage?.let { String.format("%.1f", it) } ?: "N/A"
    
    val genreNames: String
        get() = genres?.joinToString(", ") { it.name } ?: ""
    
    val director: String?
        get() = credits?.crew?.firstOrNull { it.job == "Director" }?.name
    
    val mainCast: List<CastMember>
        get() = credits?.cast?.take(10) ?: emptyList()
}

data class ProductionCompany(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("logoPath")
    val logoPath: String?,
    
    @SerializedName("originCountry")
    val originCountry: String?
)

data class ProductionCountry(
    @SerializedName("iso_3166_1")
    val iso: String,
    
    @SerializedName("name")
    val name: String
)

data class SpokenLanguage(
    @SerializedName("iso_639_1")
    val iso: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("englishName")
    val englishName: String?
)

data class Credits(
    @SerializedName("cast")
    val cast: List<CastMember>,
    
    @SerializedName("crew")
    val crew: List<CrewMember>
)

data class CastMember(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("character")
    val character: String,
    
    @SerializedName("profilePath")
    val profilePath: String?,
    
    @SerializedName("order")
    val order: Int,
    
    @SerializedName("knownForDepartment")
    val knownForDepartment: String?
)

data class CrewMember(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("job")
    val job: String,
    
    @SerializedName("department")
    val department: String,
    
    @SerializedName("profilePath")
    val profilePath: String?
)

data class Video(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("key")
    val key: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("site")
    val site: String,
    
    @SerializedName("size")
    val size: Int?,
    
    @SerializedName("official")
    val official: Boolean?
) {
    val youtubeUrl: String
        get() = "https://www.youtube.com/watch?v=$key"
    
    val youtubeThumbnail: String
        get() = "https://img.youtube.com/vi/$key/hqdefault.jpg"
}

data class WatchProviderData(
    @SerializedName("link")
    val link: String?,
    
    @SerializedName("flatrate")
    val flatrate: List<Provider>?,
    
    @SerializedName("rent")
    val rent: List<Provider>?,
    
    @SerializedName("buy")
    val buy: List<Provider>?
)

data class Provider(
    @SerializedName("providerId")
    val providerId: Int,
    
    @SerializedName("providerName")
    val providerName: String,
    
    @SerializedName("logoPath")
    val logoPath: String?
)

// Legacy support - kept for backward compatibility
data class Rating(
    @SerializedName("Source")
    val source: String,
    
    @SerializedName("Value")
    val value: String
)

data class StreamingData(
    @SerializedName("sources")
    val sources: List<WatchmodeSource>?,
    
    @SerializedName("message")
    val message: String?
)

// Response wrapper for videos
data class VideosResponse(
    @SerializedName("videos")
    val videos: List<Video>
)

// Response wrapper for watch providers
data class WatchProvidersResponse(
    @SerializedName("country")
    val country: String,
    
    @SerializedName("providers")
    val providers: WatchProviderData?
)
