package com.darklandsmobile

import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.systems.CityEventSystem
import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.systems.ReputationSystem
import com.darklandsmobile.systems.SaveLoadSystem
import com.darklandsmobile.ui.DemoFlowController
import com.darklandsmobile.world.CityCatalogue
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
        assertTrue(menu.contains("Darklands Internal Demo"))

        val selected = demo.select("praha")
        assertTrue(selected.contains("Current city: praha"))

        val slice = demo.openCurrentSlice()
        assertTrue(slice.contains("PRAHA SLICE"))

        val updated = demo.addPlaytestNote("Praha flow feels strong.")
        assertTrue(updated.contains("Session notes: 1"))
    }
}
