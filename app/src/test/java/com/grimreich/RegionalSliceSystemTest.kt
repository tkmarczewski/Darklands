package com.grimreich

import com.grimreich.core.PlayerState
import com.grimreich.core.WorldMap
import com.grimreich.systems.CityEventSystem
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.RegionalSliceSystem
import com.grimreich.systems.ReputationSystem
import com.grimreich.ui.RegionalSliceScreen
import com.grimreich.world.CityCatalogue
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
