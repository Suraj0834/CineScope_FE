package com.mrnoone.cinescope.data.api

import com.mrnoone.cinescope.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PersonService {
    
    @GET("api/person/search")
    suspend fun searchPeople(
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): ApiResponse<PersonSearchResponse>
    
    @GET("api/person/{personId}")
    suspend fun getPersonDetail(
        @Path("personId") personId: Int
    ): ApiResponse<Person>
    
    @GET("api/person/{personId}/movies")
    suspend fun getPersonFilmography(
        @Path("personId") personId: Int
    ): ApiResponse<Filmography>
    
    @GET("api/person/{personId}/images")
    suspend fun getPersonImages(
        @Path("personId") personId: Int
    ): ApiResponse<PersonImages>
    
    @GET("api/person/popular/list")
    suspend fun getPopularPeople(
        @Query("page") page: Int = 1
    ): ApiResponse<PersonSearchResponse>
}
