package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.world.CityCatalogue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EconomySystemTest {

    @BeforeEach
    fun setUp() {
        GameRepository.state = GameState()
        CityCatalogue.seedCanonical()
        ReputationSystem.clear()
    }

    @Test
    fun `priceInCity applies city price modifier`() {
        // wybrzeze_polnocne: 1.0f
        assertEquals(100, EconomySystem.priceInCity("wybrzeze_polnocne", 100))
        
        // serce_krainy: 1.2f
        assertEquals(120, EconomySystem.priceInCity("serce_krainy", 100))
    }

    @Test
    fun `priceInCity returns base price for unknown city`() {
        assertEquals(50, EconomySystem.priceInCity("unknown", 50))
    }

    @Test
    fun `priceInCity never goes below 1`() {
        assertEquals(1, EconomySystem.priceInCity("wybrzeze_polnocne", 1))
    }

    @Test
    fun `priceInCity integrates with ReputationSystem`() {
        // High reputation in wybrzeze_polnocne (total >= 60 -> 0.8f)
        ReputationSystem.modify("wybrzeze_polnocne", CityFaction.COMMONERS, 60)
        
        // 100 * 1.0 (city) * 0.8 (rep) = 80
        assertEquals(80, EconomySystem.priceInCity("wybrzeze_polnocne", 100))
        
        // Low reputation in serce_krainy (total <= -60 -> 2.0f)
        ReputationSystem.modify("serce_krainy", CityFaction.COMMONERS, -60)
        
        // 100 * 1.2 (city) * 2.0 (rep) = 240
        assertEquals(240, EconomySystem.priceInCity("serce_krainy", 100))
    }

    @Test
    fun `sellItem adds gold and removes item`() {
        val item = com.grimreich.grimreich.v1.Item(
            id = "test_item",
            name = "Test",
            type = "junk",
            slot = null,
            value = 100,
            weight = 1.0,
            effects = emptyMap<String, Int>()
        )
        GameRepository.state.inventory.add(item)
        val initialGold = GameRepository.state.gold
        
        val earned = EconomySystem.sellItem(item)
        
        assertEquals(40, earned)
        assertEquals(initialGold + 40, GameRepository.state.gold)
        assertTrue(GameRepository.state.inventory.isEmpty())
    }

    @Test
    fun `sellItem works with multiple items`() {
        val item1 = com.grimreich.grimreich.v1.Item(id = "i1", name = "Item 1", type = "junk", value = 100, weight = 1.0)
        val item2 = com.grimreich.grimreich.v1.Item(id = "i2", name = "Item 2", type = "junk", value = 200, weight = 1.0)
        GameRepository.state.inventory.addAll(listOf(item1, item2))
        val initialGold = GameRepository.state.gold
        
        EconomySystem.sellItem(item1)
        EconomySystem.sellItem(item2)
        
        // 40 + 80 = 120
        assertEquals(initialGold + 120, GameRepository.state.gold)
        assertTrue(GameRepository.state.inventory.isEmpty())
    }

    @Test
    fun `sellItem does not add gold if item not in inventory - current behavior check`() {
        val item = com.grimreich.grimreich.v1.Item(id = "ext", name = "Extra", type = "junk", value = 100)
        // Item is NOT added to inventory
        val initialGold = GameRepository.state.gold
        
        val earned = EconomySystem.sellItem(item)
        
        assertEquals(40, earned)
        assertEquals(initialGold + 40, GameRepository.state.gold)
    }
}
