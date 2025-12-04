package com.cinescope.app.data.api

import android.content.Context
import com.cinescope.app.BuildConfig
import com.cinescope.app.util.Constants
import com.cinescope.app.util.PreferenceManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    
    private var retrofit: Retrofit? = null
    private lateinit var preferenceManager: PreferenceManager
    
    fun initialize(context: Context) {
        preferenceManager = PreferenceManager(context)
    }
    
    private fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(authInterceptor())
                .addInterceptor(loggingInterceptor())
                .build()
            
            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
    
    private fun authInterceptor() = Interceptor { chain ->
        val request = chain.request()
        val token = preferenceManager.getToken()
        
        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        
        chain.proceed(newRequest)
    }
    
    private fun loggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return interceptor
    }
    
    fun <T> createService(serviceClass: Class<T>): T {
        return getRetrofit().create(serviceClass)
    }
    
    val authService: AuthService by lazy {
        createService(AuthService::class.java)
    }
    
    val moviesService: MoviesService by lazy {
        createService(MoviesService::class.java)
    }
    
    val aiService: AiService by lazy {
        createService(AiService::class.java)
    }
    
    val profileService: ProfileService by lazy {
        createService(ProfileService::class.java)
    }
    
    val personService: PersonService by lazy {
        createService(PersonService::class.java)
    }
}
