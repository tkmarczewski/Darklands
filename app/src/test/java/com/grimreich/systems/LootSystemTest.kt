package com.grimreich.systems

import com.grimreich.core.GameState
import com.grimreich.core.GameRepository
import com.grimreich.core.LootTable
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.ItemCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class LootSystemTest {

    private val gameRepository = mock<GameRepository>()
    private val itemCatalogue = mock<ItemCatalogue>()
    private val lootSystem = LootSystem(gameRepository, itemCatalogue, com.grimreich.core.DefaultCombatRandomProvider())

    @Test
    fun `awardLootFromTableDirect awards gold correctly`() {
        val state = GameState().apply { gold = 0 }
        val lootTable = LootTable(goldMin = 10, goldMax = 10)
        
        lootSystem.awardLootFromTableDirect(state, lootTable)
        
        assertEquals(10, state.gold)
    }

    @Test
    fun `awardLootFromTableDirect awards items based on chance`() {
        val state = GameState()
        val sword = Item(instanceId = "sword_1", templateId = "sword", name = "Miecz", type = "weapon", slot = "weapon")
        whenever(itemCatalogue.createInstance("sword")).thenReturn(sword)
        
        // 100% chance
        val lootTable = LootTable(goldMin = 0, goldMax = 0, itemChances = mapOf("sword" to 1.1f))
        
        val messages = lootSystem.awardLootFromTableDirect(state, lootTable)
        
        assertEquals(1, state.inventory.size)
        assertEquals("sword", state.inventory[0].templateId)
        assertTrue(messages.any { it.contains("Miecz") })
    }

    @Test
    fun `awardLootFromTableDirect does not award items if chance fails`() {
        val state = GameState()
        val sword = Item(instanceId = "sword_1", templateId = "sword", name = "Miecz", type = "weapon", slot = "weapon")
        whenever(itemCatalogue.createInstance("sword")).thenReturn(sword)
        
        // 0% chance
        val lootTable = LootTable(goldMin = 0, goldMax = 0, itemChances = mapOf("sword" to -0.1f))
        
        lootSystem.awardLootFromTableDirect(state, lootTable)
        
        assertEquals(0, state.inventory.size)
    }
}
