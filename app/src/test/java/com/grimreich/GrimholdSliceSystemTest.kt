package com.grimreich

import com.grimreich.core.PlayerState
import com.grimreich.core.WorldMap
import com.grimreich.systems.CityEventSystem
import com.grimreich.systems.GrimholdSliceSystem
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.ReputationSystem
import com.grimreich.ui.GrimholdSliceScreen
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GrimholdSliceSystemTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        WorldMap.clear()
        ReputationSystem.clear()
        CityEventSystem.clear()
        QuestSystem.clear()
        GrimholdSliceSystem.seed()
    }

    @Test
    fun `grimhold slice provides artwork and featured contracts`() {
        val view = GrimholdSliceSystem.view(PlayerState(currentCityId = "grimhold"))
        assertTrue(view.backgroundUrl.isNotBlank())
        assertTrue(view.quests.size >= 3)

        val rendered = GrimholdSliceScreen.render(view)
        assertTrue(rendered.contains("MAGDEBURG VERTICAL SLICE"))
        assertTrue(rendered.contains("Featured contracts"))
    }
}
