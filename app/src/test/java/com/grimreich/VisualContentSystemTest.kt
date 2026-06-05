package com.grimreich

import com.grimreich.core.PlayerState
import com.grimreich.core.WorldMap
import com.grimreich.systems.CityEventSystem
import com.grimreich.systems.ExpandedContentSeeder
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.ReputationSystem
import com.grimreich.systems.VisualContentSystem
import com.grimreich.ui.RichGameplayScreens
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VisualContentSystemTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        WorldMap.clear()
        ReputationSystem.clear()
        CityEventSystem.clear()
        QuestSystem.clear()
        ExpandedContentSeeder.seed(seed = 21)
    }

    @Test
    fun `city hub exposes mood visuals and quest cards`() {
        val hub = VisualContentSystem.cityHub(PlayerState(currentCityId = "grimhold"))
        assertTrue(hub.cityTitle == "Grimhold")
        assertTrue(hub.backdropName.isNotBlank())
        assertTrue(hub.questCards.isNotEmpty())

        val rendered = RichGameplayScreens.renderCityHub(hub)
        assertTrue(rendered.contains("HUB"))
        assertTrue(rendered.contains("Quest board"))
    }
}
