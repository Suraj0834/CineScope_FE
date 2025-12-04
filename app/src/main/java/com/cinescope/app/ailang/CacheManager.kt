package com.cinescope.app.ailang

import android.content.Context
import java.io.File
import java.util.Properties

class CacheManager(private val context: Context) {

    private val cacheDir = context.filesDir

    fun getCachedTranslation(langCode: String): Map<String, String>? {
        val file = File(cacheDir, "strings_$langCode.txt")
        if (!file.exists()) return null

        return parseFile(file.readText())
    }

    fun saveTranslation(langCode: String, content: Map<String, String>, version: Int) {
        val file = File(cacheDir, "strings_$langCode.txt")
        val sb = StringBuilder()
        sb.append("version = $version\n")
        content.forEach { (key, value) ->
            if (key != "version") {
                sb.append("$key = $value\n")
            }
        }
        file.writeText(sb.toString())
    }

    fun getMasterFile(): Map<String, String> {
        val content = context.assets.open("strings_en.txt").bufferedReader().use { it.readText() }
        return parseFile(content)
    }

    private fun parseFile(content: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        content.lines().forEach { line ->
            if (line.contains("=")) {
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val value = parts[1].trim()
                    map[key] = value
                }
            }
        }
        return map
    }

    fun getVersion(map: Map<String, String>): Int {
        return map["version"]?.toIntOrNull() ?: 0
    }

    fun clearCache(langCode: String) {
        val file = File(cacheDir, "strings_$langCode.txt")
        if (file.exists()) {
            file.delete()
        }
    }

    fun clearAllCache() {
        cacheDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("strings_") && file.name.endsWith(".txt") && file.name != "strings_en.txt") {
                file.delete()
            }
        }
    }
}
