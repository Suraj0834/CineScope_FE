package com.cinescope.app.ailang.models

data class TranslationResponse(
    val language: String,
    val translations: Map<String, String>
)

data class AiRequest(
    val targetLanguage: String,
    val sourceContent: Map<String, String>
)
