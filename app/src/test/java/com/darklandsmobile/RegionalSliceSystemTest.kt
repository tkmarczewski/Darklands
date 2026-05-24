package com.darklandsmobile

import com.darklandsmobile.core.PlayerState
import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.systems.CityEventSystem
import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.systems.RegionalSliceSystem
import com.darklandsmobile.systems.ReputationSystem
import com.darklandsmobile.ui.RegionalSliceScreen
import com.darklandsmobile.world.CityCatalogue
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegionalSliceSystemTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        WorldMap.clear()
        ReputationSystem.clear()
        CityEventSystem.clear()
        QuestSystem.clear()
        RegionalSliceSystem.seedAll()
    }

    @Test
    fun `all regional slices expose artwork and contracts`() {
        val cities = listOf("praha", "koln", "brno", "wroclaw", "vienna")
        cities.forEach { cityId ->
            val view = RegionalSliceSystem.view(cityId, PlayerState(currentCityId = cityId))
            assertTrue(view.backgroundUrl.isNotBlank())
            assertTrue(view.quests.isNotEmpty())
            val rendered = RegionalSliceScreen.render(view)
            assertTrue(rendered.contains("SLICE"))
            assertTrue(rendered.contains("Featured contracts"))
        }
    }
}
