package com.cinescope.app.data.repository

import com.cinescope.app.data.api.ApiClient
import com.cinescope.app.data.api.ForgotPasswordRequest
import com.cinescope.app.data.api.LoginRequest
import com.cinescope.app.data.api.RegisterRequest
import com.cinescope.app.data.api.VerifyOtpRequest
import com.cinescope.app.data.model.ApiResponse
import com.cinescope.app.data.model.AuthResponse

class AuthRepository {
    
    private val authService = ApiClient.authService
    
    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(name, email, password)
            val response = authService.register(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = authService.login(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun forgotPassword(email: String): Result<ApiResponse<Any>> {
        return try {
            val request = ForgotPasswordRequest(email)
            val response = authService.forgotPassword(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verifyOtp(email: String, otp: String, newPassword: String): Result<ApiResponse<Any>> {
        return try {
            val request = VerifyOtpRequest(email, otp, newPassword)
            val response = authService.verifyOtp(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
