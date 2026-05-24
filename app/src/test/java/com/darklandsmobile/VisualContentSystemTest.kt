package com.darklandsmobile

import com.darklandsmobile.core.PlayerState
import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.systems.CityEventSystem
import com.darklandsmobile.systems.ExpandedContentSeeder
import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.systems.ReputationSystem
import com.darklandsmobile.systems.VisualContentSystem
import com.darklandsmobile.ui.RichGameplayScreens
import com.darklandsmobile.world.CityCatalogue
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
        val hub = VisualContentSystem.cityHub(PlayerState(currentCityId = "magdeburg"))
        assertTrue(hub.cityTitle == "Magdeburg")
        assertTrue(hub.backdropName.isNotBlank())
        assertTrue(hub.questCards.isNotEmpty())

        val rendered = RichGameplayScreens.renderCityHub(hub)
        assertTrue(rendered.contains("HUB"))
        assertTrue(rendered.contains("Quest board"))
    }
}
