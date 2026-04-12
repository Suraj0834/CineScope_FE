package com.mrnoone.cinescope.ailang

import android.content.Context
import android.util.Log

/**
 * AiLang - Simplified localization system (stub version)
 *
 * Translation engine disabled for security (API key removed from app).
 * For multilingual support, implement server-side translation or use
 * Android's built-in localization (strings.xml).
 *
 * Currently returns keys as-is (English only).
 */
object AiLang {

    private const val TAG = "AiLang"
    private var currentLanguage: String = "en"

    // Callback for UI updates (kept for compatibility)
    private val listeners = mutableListOf<() -> Unit>()

    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it.invoke() }
    }

    fun init(context: Context) {
        Log.d(TAG, "AiLang initialized (stub mode - English only)")
        // Stub implementation - no translation engine
        currentLanguage = "en"
    }

    /**
     * Translate function - currently returns key as-is
     * For production, implement proper localization using Android resources
     */
    fun t(key: String): String {
        // Stub: Return key as-is (no translation)
        return key
    }

    fun setLanguage(langCode: String) {
        Log.d(TAG, "Language change requested to $langCode (ignored - English only)")
        // Stub: Keep English
        currentLanguage = "en"
        notifyListeners()
    }

    fun forceRefresh() {
        Log.d(TAG, "Force refresh requested (ignored)")
        // Stub: Nothing to refresh
        notifyListeners()
    }

    fun getLanguage(): String = currentLanguage
}
