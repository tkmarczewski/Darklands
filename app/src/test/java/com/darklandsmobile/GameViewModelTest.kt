package com.darklandsmobile

import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.systems.CityEventSystem
import com.darklandsmobile.systems.GameViewModel
import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.systems.ReputationSystem
import com.darklandsmobile.systems.SaveLoadSystem
import com.darklandsmobile.world.CityCatalogue
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameViewModelTest {

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
    fun `view model supports accept travel resolve save and load`() {
        val vm = GameViewModel()
        val quest = vm.cityScreenState.availableQuests.first()

        vm.acceptQuest(quest.id)
        assertTrue(vm.playerState.activeQuestId == quest.id)

        vm.travelToActiveQuest()
        assertNotNull(vm.travelScreenState)

        vm.resolveActiveQuest()
        assertTrue(vm.playerState.activeQuestId == null)
        assertNotNull(vm.resolutionScreenState)
        assertTrue(SaveLoadSystem.hasSave())

        val loaded = vm.loadGame()
        assertNotNull(loaded)
        assertTrue(vm.playerState.completedQuestIds.contains(quest.id))
    }
}
