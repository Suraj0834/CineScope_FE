package com.cinescope.app.data.repository

import com.cinescope.app.data.api.ApiClient
import com.cinescope.app.data.model.*

class PersonRepository {
    
    private val personService = ApiClient.personService
    
    /**
     * Search for people (actors, directors, etc.)
     */
    suspend fun searchPeople(query: String, page: Int = 1): Result<ApiResponse<PersonSearchResponse>> {
        return try {
            val response = personService.searchPeople(query, page)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get person details by ID
     */
    suspend fun getPersonDetail(personId: Int): Result<ApiResponse<Person>> {
        return try {
            val response = personService.getPersonDetail(personId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get person's filmography (all movies)
     */
    suspend fun getPersonFilmography(personId: Int): Result<ApiResponse<Filmography>> {
        return try {
            val response = personService.getPersonFilmography(personId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get person's images/photos
     */
    suspend fun getPersonImages(personId: Int): Result<ApiResponse<PersonImages>> {
        return try {
            val response = personService.getPersonImages(personId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get popular people
     */
    suspend fun getPopularPeople(page: Int = 1): Result<ApiResponse<PersonSearchResponse>> {
        return try {
            val response = personService.getPopularPeople(page)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
