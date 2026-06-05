package com.grimreich.systems

import android.content.Context
import com.google.gson.Gson
import com.grimreich.core.GameState
import com.grimreich.core.GameRepository

/**
 * Persistent save system using SharedPreferences and Gson.
 */
object SaveLoadSystem {
    private const val PREFS_NAME = "grimreich_save"
    private const val KEY_SAVE = "save_state"
    private val gson = Gson()

    fun save(context: Context) {
        val state = GameRepository.state
        val json = gson.toJson(state)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SAVE, json)
            .apply()
    }

    fun load(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SAVE, null) ?: return false
        return try {
            val loadedState = gson.fromJson(json, GameState::class.java)
            GameRepository.state = loadedState
            true
        } catch (e: Exception) {
            false
        }
    }

    fun hasSave(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).contains(KEY_SAVE)
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().remove(KEY_SAVE).apply()
    }
}
