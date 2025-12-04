package com.cinescope.app.data.api

import com.cinescope.app.data.model.ApiResponse
import com.cinescope.app.data.model.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse
    
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse
    
    @POST("api/auth/forgot")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): ApiResponse<Any>
    
    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): ApiResponse<Any>
}

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)
