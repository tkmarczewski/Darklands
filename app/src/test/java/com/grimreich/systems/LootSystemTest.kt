package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.world.ItemCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LootSystemTest {

    @Before
    fun setUp() {
        GameRepository.state = GameState()
        ItemCatalogue.seed()
    }

    @Test
    fun `awardLoot adds item to inventory and returns message`() {
        // Force 100% chance
        val msg = LootSystem.awardLoot(chance = 1.1f)
        
        assertTrue(msg.contains("Znaleziono przedmiot"))
        assertEquals(1, GameRepository.state.inventory.size)
        
        val item = GameRepository.state.inventory.first()
        assertTrue(msg.contains(item.name))
    }

    @Test
    fun `awardLoot returns empty string when no loot rolled`() {
        // Force 0% chance
        val msg = LootSystem.awardLoot(chance = -0.1f)
        
        assertEquals("", msg)
        assertTrue(GameRepository.state.inventory.isEmpty())
    }
}
