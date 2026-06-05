package com.grimreich

import com.grimreich.systems.CityEventSystem
import com.grimreich.systems.CityFaction
import com.grimreich.systems.ReputationSystem
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CityEventSystemTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        ReputationSystem.clear()
        CityEventSystem.clear()
        CityCatalogue.seedSprint1()
        CityEventSystem.seedStage1Events()
    }

    @Test
    fun `every city gets at least two stage 1 events`() {
        CityCatalogue.all().forEach { city ->
            val events = CityEventSystem.getEventsForCity(city.id)
            assertTrue("Expected >= 2 events for ${city.id}", events.size >= 2)
        }
    }

    @Test
    fun `general event is available without reputation`() {
        val available = CityEventSystem.getAvailableEventsForCity("koln")
        assertTrue(available.any { it.id == "koln_general_event" })
    }

    @Test
    fun `merchant gated event unlocks with enough city reputation`() {
        val before = CityEventSystem.getAvailableEventsForCity("koln")
        assertTrue(before.none { it.id == "koln_guild_pressure" })

        ReputationSystem.modify("koln", CityFaction.MERCHANTS, 10)
        val after = CityEventSystem.getAvailableEventsForCity("koln")
        assertTrue(after.any { it.id == "koln_guild_pressure" })
    }
}
