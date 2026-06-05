package com.grimreich

import com.grimreich.core.GameState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SaveLoadSystemTest {

    @Test
    fun `gson can serialize and deserialize full game state`() {
        val original = GameState(gold = 555, grimCurrentRegion = "TestRegion")
        val gson = com.google.gson.Gson()
        val json = gson.toJson(original)
        val loaded = gson.fromJson(json, GameState::class.java)
        
        assertEquals(original.gold, loaded.gold)
        assertEquals(original.grimCurrentRegion, loaded.grimCurrentRegion)
    }
}
