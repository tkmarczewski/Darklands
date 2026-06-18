package com.grimreich.core

import com.google.gson.Gson
import java.io.File

class GameRootStateSaver(
    private val gson: Gson,
    private val fileProvider: () -> File
) {

    fun save(gameState: GameState) {
        val file = fileProvider()
        try {
            file.writeText(gson.toJson(gameState))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun load(): GameState? {
        val file = fileProvider()
        if (!file.exists()) return null
        return try {
            gson.fromJson(file.readText(), GameState::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
