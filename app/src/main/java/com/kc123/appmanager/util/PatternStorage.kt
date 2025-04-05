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
}
