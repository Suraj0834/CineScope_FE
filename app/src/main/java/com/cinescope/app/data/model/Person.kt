package com.cinescope.app.data.model

import com.google.gson.annotations.SerializedName

data class Person(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("biography")
    val biography: String?,
    
    @SerializedName("birthday")
    val birthday: String?,
    
    @SerializedName("deathday")
    val deathday: String?,
    
    @SerializedName("placeOfBirth")
    val placeOfBirth: String?,
    
    @SerializedName("profilePath")
    val profilePath: String?,
    
    @SerializedName("knownForDepartment")
    val knownForDepartment: String?,
    
    @SerializedName("popularity")
    val popularity: Double?,
    
    @SerializedName("gender")
    val gender: Int?, // 0: Not specified, 1: Female, 2: Male, 3: Non-binary
    
    @SerializedName("adult")
    val adult: Boolean?,
    
    @SerializedName("alsoKnownAs")
    val alsoKnownAs: List<String>?,
    
    @SerializedName("homepage")
    val homepage: String?,
    
    @SerializedName("imdbId")
    val imdbId: String?
) {
    val displayGender: String
        get() = when (gender) {
            1 -> "Female"
            2 -> "Male"
            3 -> "Non-binary"
            else -> "Not specified"
        }
    
    val age: Int?
        get() = birthday?.let { birth ->
            val birthYear = birth.take(4).toIntOrNull() ?: return null
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val deathYear = deathday?.take(4)?.toIntOrNull()
            (deathYear ?: currentYear) - birthYear
        }
    
    val displayAge: String
        get() = age?.let { "$it years old" } ?: ""
}

data class PersonSearchResult(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("profilePath")
    val profilePath: String?,
    
    @SerializedName("knownForDepartment")
    val knownForDepartment: String?,
    
    @SerializedName("popularity")
    val popularity: Double?,
    
    @SerializedName("knownFor")
    val knownFor: List<KnownForItem>?
)

data class KnownForItem(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("title")
    val title: String?,
    
    @SerializedName("mediaType")
    val mediaType: String?,
    
    @SerializedName("posterPath")
    val posterPath: String?
)

data class PersonSearchResponse(
    @SerializedName("query")
    val query: String?,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("totalPages")
    val totalPages: Int,
    
    @SerializedName("totalResults")
    val totalResults: Int,
    
    @SerializedName("people")
    val people: List<PersonSearchResult>
)

data class Filmography(
    @SerializedName("totalCast")
    val totalCast: Int,
    
    @SerializedName("totalCrew")
    val totalCrew: Int,
    
    @SerializedName("cast")
    val cast: List<FilmographyMovie>,
    
    @SerializedName("crew")
    val crew: List<FilmographyCrewMovie>,
    
    @SerializedName("crewByDepartment")
    val crewByDepartment: Map<String, List<FilmographyCrewMovie>>?
)

data class FilmographyMovie(
    @SerializedName("id")
    val tmdbId: Int,
    
    @SerializedName("imdbId")
    val imdbId: String?,
    
    @SerializedName("traktId")
    val traktId: Int?,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("character")
    val character: String?,
    
    @SerializedName("releaseDate")
    val releaseDate: String?,
    
    @SerializedName("posterPath")
    val posterPath: String?,
    
    @SerializedName("voteAverage")
    val voteAverage: Double?,
    
    @SerializedName("popularity")
    val popularity: Double?,
    
    @SerializedName("overview")
    val overview: String?
) {
    val displayYear: String
        get() = releaseDate?.take(4) ?: "TBA"
}

data class FilmographyCrewMovie(
    @SerializedName("tmdbId")
    val tmdbId: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("job")
    val job: String?,
    
    @SerializedName("department")
    val department: String?,
    
    @SerializedName("releaseDate")
    val releaseDate: String?,
    
    @SerializedName("posterPath")
    val posterPath: String?,
    
    @SerializedName("voteAverage")
    val voteAverage: Double?,
    
    @SerializedName("popularity")
    val popularity: Double?,
    
    @SerializedName("overview")
    val overview: String?
) {
    val displayYear: String
        get() = releaseDate?.take(4) ?: "TBA"
}

data class PersonImages(
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("images")
    val images: List<PersonImage>
)

data class PersonImage(
    @SerializedName("filePath")
    val filePath: String?,
    
    @SerializedName("width")
    val width: Int?,
    
    @SerializedName("height")
    val height: Int?,
    
    @SerializedName("aspectRatio")
    val aspectRatio: Double?,
    
    @SerializedName("voteAverage")
    val voteAverage: Double?,
    
    @SerializedName("voteCount")
    val voteCount: Int?
)
