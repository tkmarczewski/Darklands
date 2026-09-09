package com.grimreich.systems

import com.grimreich.core.FactionReputationSystem
import com.grimreich.core.GameConstants
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.CityCatalogue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EconomySystemTest {
    private lateinit var economySystem: EconomySystem
    private val factionReputationSystem = mock<FactionReputationSystem>()
    private val cityCatalogue = mock<CityCatalogue>()

    @Before
    fun setup() {
        economySystem = EconomySystem(factionReputationSystem, cityCatalogue)
    }

    @Test
    fun `calculateSellPrice should use merchants reputation as fallback`() {
        val item = Item(instanceId = "i1", templateId = "t1", name = "Test", type = "misc", value = 100)
        
        // Mock city with no specific ruling faction (null/empty)
        whenever(cityCatalogue.get("city_1")).thenReturn(null)
        
        // Mock positive reputation for "merchants" (lowercase)
        // If AUD-03 is fixed, it will look up "merchants"
        whenever(factionReputationSystem.getReputation("merchants")).thenReturn(50)
        
        val price = economySystem.calculateSellPrice("city_1", item)
        
        // Base sell price = item.value (100) * regional (1.0) * rep (buyMod(50) = 1.0 - 50*0.02 = 0.0)
        // Wait, buyModifier(rep) = 1.0 - rep * 0.02 = 1.0 - 1.0 = 0.0? No, sellModifier is 1.0 + rep * 0.02 = 1.0 + 1.0 = 2.0
        // baseSellPrice = 100 * 1.0 * 2.0 * 0.5 = 100.
        
        // If it was still "MERCHANTS", reputation lookup would return 0 (neutral) -> price = 100 * 1 * 1 * 0.5 = 50.
        
        assertTrue(price > 50, "Price should be higher than neutral (50) due to Merchants reputation. Actual: $price")
        assertEquals(65, price)
    }
}
