package com.cinescope.app.ailang

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*

object AiLang {

    private const val TAG = "AiLang"
    private lateinit var cacheManager: CacheManager
    private lateinit var translationEngine: TranslationEngine
    private lateinit var languageManager: LanguageManager
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var currentTranslations: Map<String, String> = emptyMap()
    private var masterTranslations: Map<String, String> = emptyMap()

    // Callback for UI updates
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
        cacheManager = CacheManager(context)
        translationEngine = TranslationEngine()
        languageManager = LanguageManager(context)

        // Load master file (English)
        masterTranslations = cacheManager.getMasterFile()

        // Load current language
        loadLanguage(languageManager.currentLanguage)
    }

    fun t(key: String): String {
        return currentTranslations[key] ?: masterTranslations[key] ?: key
    }

    fun setLanguage(langCode: String) {
        if (langCode == languageManager.currentLanguage) return
        
        // Clear cache for this language to force fresh translation
        cacheManager.clearCache(langCode)
        
        languageManager.currentLanguage = langCode
        loadLanguage(langCode)
    }

    fun forceRefresh() {
        cacheManager.clearAllCache()
        loadLanguage(languageManager.currentLanguage)
    }

    fun getLanguage(): String = languageManager.currentLanguage

    private fun loadLanguage(langCode: String) {
        scope.launch(Dispatchers.IO) {
            if (langCode == "en") {
                currentTranslations = masterTranslations
                withContext(Dispatchers.Main) { notifyListeners() }
                return@launch
            }

            // 1. Check Cache
            val cached = cacheManager.getCachedTranslation(langCode)
            val masterVersion = cacheManager.getVersion(masterTranslations)

            if (cached != null) {
                val cachedVersion = cacheManager.getVersion(cached)
                if (cachedVersion >= masterVersion) {
                    // Cache is up to date
                    currentTranslations = cached
                    withContext(Dispatchers.Main) { notifyListeners() }
                    return@launch
                }
            }

            // 2. Need Translation (Cache missing or outdated)
            Log.d(TAG, "Translating to $langCode...")
            translationEngine.translate(
                targetLang = langCode,
                sourceContent = masterTranslations,
                onSuccess = { translatedMap ->
                    // Save to cache
                    cacheManager.saveTranslation(langCode, translatedMap, masterVersion)
                    currentTranslations = translatedMap
                    
                    scope.launch(Dispatchers.Main) {
                        notifyListeners()
                    }
                },
                onError = { error ->
                    Log.e(TAG, "Translation failed: $error")
                    // Fallback to English or partial cache
                    if (cached != null) {
                        currentTranslations = cached
                        scope.launch(Dispatchers.Main) { notifyListeners() }
                    }
                }
            )
        }
    }
}
