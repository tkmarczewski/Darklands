package com.grimreich

import com.grimreich.core.WorldMap
import com.grimreich.systems.CityEventSystem
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.ReputationSystem
import com.grimreich.systems.SaveLoadSystem
import com.grimreich.ui.DemoFlowController
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DemoFlowControllerTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        WorldMap.clear()
        ReputationSystem.clear()
        CityEventSystem.clear()
        QuestSystem.clear()
        SaveLoadSystem.clear()
    }

    @Test
    fun `demo shell supports selection slice opening and notes`() {
        val demo = DemoFlowController()
        val menu = demo.mainMenu()
        assertTrue(menu.contains("Grimreich Internal Demo"))

        val selected = demo.select("praha")
        assertTrue(selected.contains("Current city: praha"))

        val slice = demo.openCurrentSlice()
        assertTrue(slice.contains("PRAHA SLICE"))

        val updated = demo.addPlaytestNote("Praha flow feels strong.")
        assertTrue(updated.contains("Session notes: 1"))
    }
}
