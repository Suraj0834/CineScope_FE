package com.cinescope.app.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferenceManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        EncryptedSharedPreferences.create(
            context,
            Constants.PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback to regular SharedPreferences if EncryptedSharedPreferences fails
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveToken(token: String) {
        sharedPreferences.edit().putString(Constants.KEY_TOKEN, token).apply()
    }
    
    fun getToken(): String? {
        return sharedPreferences.getString(Constants.KEY_TOKEN, null)
    }
    
    fun clearToken() {
        sharedPreferences.edit().remove(Constants.KEY_TOKEN).apply()
    }
    
    fun saveUserInfo(name: String, email: String) {
        sharedPreferences.edit()
            .putString(Constants.KEY_USER_NAME, name)
            .putString(Constants.KEY_USER_EMAIL, email)
            .apply()
    }
    
    fun getUserName(): String? {
        return sharedPreferences.getString(Constants.KEY_USER_NAME, null)
    }
    
    fun getUserEmail(): String? {
        return sharedPreferences.getString(Constants.KEY_USER_EMAIL, null)
    }
    
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun isNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean("notifications_enabled", true)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("dark_mode_enabled", enabled).apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean("dark_mode_enabled", true)
    }
}
