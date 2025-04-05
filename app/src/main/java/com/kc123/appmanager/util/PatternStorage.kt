package com.kc123.appmanager.util

import android.content.Context
import android.content.SharedPreferences

object PatternStorage {
    private const val PREF_NAME = "pattern_prefs"
    private const val KEY_PREFIX = "pattern_"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun savePattern(context: Context, packageName: String, pattern: List<Pair<Int, Int>>) {
        val serialized = pattern.joinToString(",") { "${it.first}-${it.second}" }
        getPrefs(context).edit().putString(KEY_PREFIX + packageName, serialized).apply()
    }

    fun loadPattern(context: Context, packageName: String): List<Pair<Int, Int>> {
        val data = getPrefs(context).getString(KEY_PREFIX + packageName, "") ?: return emptyList()
        return data.split(",")
            .mapNotNull {
                val parts = it.split("-")
                if (parts.size == 2) {
                    val row = parts[0].toIntOrNull()
                    val col = parts[1].toIntOrNull()
                    if (row != null && col != null) Pair(row, col) else null
                } else null
            }
    }

    fun isPatternAlreadyUsed(context: Context, newPattern: List<Pair<Int, Int>>, currentPackage: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val newSerialized = newPattern.joinToString(",") { "${it.first}-${it.second}" }

        return prefs.all.any { (key, value) ->
            key != "$KEY_PREFIX$currentPackage" &&
                    value == newSerialized
        }
    }

    fun getAllPatterns(context: Context): Map<String, List<Pair<Int, Int>>> {
        val prefs = getPrefs(context)
        val allEntries = prefs.all
        val patternMap = mutableMapOf<String, List<Pair<Int, Int>>>()

        for ((key, value) in allEntries) {
            if (key.startsWith(KEY_PREFIX) && value is String) {
                val packageName = key.removePrefix(KEY_PREFIX)
                val pattern = value.split(",").mapNotNull {
                    val parts = it.split("-")
                    if (parts.size == 2) {
                        val row = parts[0].toIntOrNull()
                        val col = parts[1].toIntOrNull()
                        if (row != null && col != null) Pair(row, col) else null
                    } else null
                }
                patternMap[packageName] = pattern
            }
        }

        return patternMap
    }


}
