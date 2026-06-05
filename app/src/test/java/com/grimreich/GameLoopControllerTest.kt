package com.grimreich

import com.grimreich.core.WorldMap
import com.grimreich.systems.CityEventSystem
import com.grimreich.systems.GameLoopController
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.ReputationSystem
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameLoopControllerTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        WorldMap.clear()
        ReputationSystem.clear()
        CityEventSystem.clear()
        QuestSystem.clear()
    }

    @Test
    fun `full playable loop works end to end`() {
        var player = GameLoopController.bootstrap(seed = 12)
        val cityScreen = GameLoopController.cityScreen(player)
        assertTrue(cityScreen.cityId == "grimhold")
        assertTrue(cityScreen.availableQuests.isNotEmpty())

        val pickedQuest = cityScreen.availableQuests.first()
        player = GameLoopController.acceptQuest(player, pickedQuest.id)
        assertNotNull(player.activeQuestId)

        val (afterTravel, travelScreen) = GameLoopController.travelToQuest(player)
        assertTrue(travelScreen.toCityId == pickedQuest.cityId)

        val (afterResolution, resolution) = GameLoopController.resolveActiveQuest(afterTravel)
        assertTrue(afterResolution.activeQuestId == null)
        assertTrue(afterResolution.completedQuestIds.contains(pickedQuest.id))
        assertTrue(afterResolution.gold >= 100)
        assertTrue(resolution.goldAfter >= resolution.goldBefore)
    }
}
