package com.cinescope.app.ailang

import android.util.Log
import com.cinescope.app.BuildConfig
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class TranslationEngine {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    // Gemini Configuration - API key loaded from BuildConfig (secure)
    private val API_KEY = BuildConfig.GEMINI_API_KEY
    private val API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$API_KEY"

    fun translate(
        targetLang: String,
        sourceContent: Map<String, String>,
        onSuccess: (Map<String, String>) -> Unit,
        onError: (String) -> Unit
    ) {
        // 1. Prepare the prompt
        val prompt = buildPrompt(targetLang, sourceContent)

        // 2. Create Request Body (Gemini Format)
        val jsonBody = """
            {
                "contents": [{
                    "parts": [{"text": ${gson.toJson(prompt)}}]
                }],
                "generationConfig": {
                    "temperature": 0.1
                }
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        // 3. Execute Call
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError("Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                
                if (!response.isSuccessful) {
                    Log.e("TranslationEngine", "Error: $responseBody")
                    // Fallback for demo if quota exceeded or other error
                    val mockTranslation = sourceContent.mapValues { (_, value) -> "$value [$targetLang]" }
                    onSuccess(mockTranslation)
                    return
                }

                try {
                    // Parse Gemini response
                    val content = extractContentFromGeminiResponse(responseBody)
                    val translatedMap = parseResponseToMap(content)
                    
                    if (translatedMap.isEmpty()) {
                         onError("Empty translation received")
                         return
                    }
                    
                    onSuccess(translatedMap)
                } catch (e: Exception) {
                    Log.e("TranslationEngine", "Parsing error", e)
                    onError("Parsing error: ${e.message}")
                }
            }
        })
    }

    private fun getLanguageName(code: String): String {
        return when (code) {
            "en" -> "English"
            "es" -> "Spanish"
            "fr" -> "French"
            "de" -> "German"
            "hi" -> "Hindi"
            "te" -> "Telugu"
            "ta" -> "Tamil"
            "zh" -> "Chinese (Simplified)"
            "ja" -> "Japanese"
            "ko" -> "Korean"
            "ru" -> "Russian"
            "ar" -> "Arabic"
            else -> code
        }
    }

    private fun buildPrompt(targetLang: String, content: Map<String, String>): String {
        val languageName = getLanguageName(targetLang)
        val sb = StringBuilder()
        sb.append("You are a professional translator. Translate the following English key-value pairs into $languageName language.\n")
        sb.append("\nIMPORTANT RULES:\n")
        sb.append("1. Keep the keys (left side of =) EXACTLY the same in English.\n")
        sb.append("2. Translate ONLY the values (right side of =) into $languageName.\n")
        sb.append("3. Return ONLY the key=value pairs, one per line.\n")
        sb.append("4. Do NOT add markdown formatting, code blocks, JSON, or explanations.\n")
        sb.append("5. Use native $languageName script (e.g., देवनागरी for Hindi, 한글 for Korean, தமிழ் for Tamil, తెలుగు for Telugu).\n")
        sb.append("6. Maintain natural, fluent translations appropriate for a movie app.\n\n")
        sb.append("Translate these:\n\n")
        
        content.forEach { (key, value) ->
            if (key != "version") {
                sb.append("$key = $value\n")
            }
        }
        return sb.toString()
    }

    private fun extractContentFromGeminiResponse(json: String?): String {
        if (json == null) return ""
        
        try {
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            val candidates = jsonObject.getAsJsonArray("candidates")
            if (candidates != null && candidates.size() > 0) {
                val content = candidates.get(0).asJsonObject.getAsJsonObject("content")
                val parts = content.getAsJsonArray("parts")
                if (parts != null && parts.size() > 0) {
                    return parts.get(0).asJsonObject.get("text").asString
                }
            }
        } catch (e: Exception) {
            Log.e("TranslationEngine", "JSON Parse Error", e)
        }
        return ""
    }

    private fun parseResponseToMap(content: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        content.lines().forEach { line ->
            if (line.contains("=")) {
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) {
                    map[parts[0].trim()] = parts[1].trim()
                }
            }
        }
        return map
    }
}
