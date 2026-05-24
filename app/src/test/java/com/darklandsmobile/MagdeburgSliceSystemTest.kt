package com.darklandsmobile

import com.darklandsmobile.core.PlayerState
import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.systems.CityEventSystem
import com.darklandsmobile.systems.MagdeburgSliceSystem
import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.systems.ReputationSystem
import com.darklandsmobile.ui.MagdeburgSliceScreen
import com.darklandsmobile.world.CityCatalogue
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MagdeburgSliceSystemTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        WorldMap.clear()
        ReputationSystem.clear()
        CityEventSystem.clear()
        QuestSystem.clear()
        MagdeburgSliceSystem.seed()
    }

    @Test
    fun `magdeburg slice provides artwork and featured contracts`() {
        val view = MagdeburgSliceSystem.view(PlayerState(currentCityId = "magdeburg"))
        assertTrue(view.backgroundUrl.isNotBlank())
        assertTrue(view.quests.size >= 3)

        val rendered = MagdeburgSliceScreen.render(view)
        assertTrue(rendered.contains("MAGDEBURG VERTICAL SLICE"))
        assertTrue(rendered.contains("Featured contracts"))
    }
}
