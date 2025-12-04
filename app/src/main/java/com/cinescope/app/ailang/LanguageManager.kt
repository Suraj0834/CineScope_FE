package com.cinescope.app.ailang

import android.content.Context
import android.content.SharedPreferences

class LanguageManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("ai_lang_prefs", Context.MODE_PRIVATE)
    private val KEY_LANG = "selected_language"

    var currentLanguage: String
        get() = prefs.getString(KEY_LANG, "en") ?: "en"
        set(value) {
            prefs.edit().putString(KEY_LANG, value).apply()
        }

    fun getSupportedLanguages(): List<String> {
        return listOf("en", "es", "fr", "de", "hi", "te", "ta", "zh", "ja", "ko", "ru", "ar")
    }

    fun getLanguageName(code: String): String {
        return when (code) {
            "en" -> "English"
            "es" -> "Spanish"
            "fr" -> "French"
            "de" -> "German"
            "hi" -> "Hindi"
            "te" -> "Telugu"
            "ta" -> "Tamil"
            "zh" -> "Chinese"
            "ja" -> "Japanese"
            "ko" -> "Korean"
            "ru" -> "Russian"
            "ar" -> "Arabic"
            else -> code
        }
    }
}
