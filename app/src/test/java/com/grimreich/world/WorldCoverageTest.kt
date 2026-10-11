package com.grimreich.world

import com.grimreich.content.city.CityData
import com.grimreich.content.city.CityType
import com.grimreich.core.Career
import com.grimreich.core.DefaultCombatRandomProvider
import org.junit.Assert.*
import org.junit.Test

class WorldCoverageTest {

    @Test
    fun testItemCatalogueSeedAndRetrieval() {
        val catalogue = ItemCatalogue()
        catalogue.seed()

        val templates = catalogue.allTemplates()
        assertFalse(templates.isEmpty())

        val swordTemplate = catalogue.findByTemplateId("sword_basic")
        assertNotNull(swordTemplate)
        assertEquals("Miecz", swordTemplate?.name)

        val swordInstance = catalogue.createInstance("sword_basic")
        assertNotNull(swordInstance)
        assertFalse(swordInstance!!.instanceId.isEmpty())

        val randomItem = catalogue.getRandomItemInstance()
        assertNotNull(randomItem)
    }

    @Test
    fun testHeroPoolGeneration() {
        val heroPool = HeroPool(DefaultCombatRandomProvider())
        val heroes = heroPool.generatePool(3)

        assertEquals(3, heroes.size)
        val firstHero = heroes.first()
        assertNotNull(firstHero.name)
        assertTrue(firstHero.hp > 0)

        val knightCost = heroPool.hireCostFor(Career.KNIGHT)
        assertEquals(120, knightCost)
    }

    @Test
    fun testCityDataAndType() {
        val cityData = CityData(
            id = "city_1",
            name = "Oakhaven",
            region = "Verdun",
            type = CityType.CITY,
            population = 5000,
            priceModifier = 1.1f
        )
        assertEquals("Oakhaven", cityData.name)
        assertEquals(CityType.CITY, cityData.type)
        assertEquals(5000, cityData.population)
    }
}
