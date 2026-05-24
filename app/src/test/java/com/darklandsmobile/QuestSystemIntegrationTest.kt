package com.darklandsmobile

import com.darklandsmobile.systems.CityEventSystem
import com.darklandsmobile.systems.QuestOriginType
import com.darklandsmobile.systems.QuestStatus
import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuestSystemIntegrationTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        CityEventSystem.clear()
        QuestSystem.clear()
        CityCatalogue.seedSprint1()
        CityEventSystem.seedStage1Events()
    }

    @Test
    fun `integrated quest feed contains city and procedural sources`() {
        QuestSystem.seedIntegratedContent(seed = 12)
        val quests = QuestSystem.all()

        assertTrue(quests.any { it.originType == QuestOriginType.CITY_EVENT })
        assertTrue(quests.any { it.originType == QuestOriginType.PROCEDURAL_LOCATION })
        assertTrue(quests.all { it.rewardGold >= 0 })
    }

    @Test
    fun `can activate and complete a quest`() {
        QuestSystem.seedIntegratedContent(seed = 12)
        val quest = QuestSystem.all().first()

        val active = QuestSystem.activate(quest.id)
        assertEquals(QuestStatus.ACTIVE, active.status)

        val completed = QuestSystem.complete(quest.id)
        assertEquals(QuestStatus.COMPLETED, completed.status)
    }

    @Test
    fun `city filter returns only quests for that city`() {
        QuestSystem.seedIntegratedContent(seed = 12)
        val cityId = "magdeburg"
        val cityQuests = QuestSystem.availableForCity(cityId)

        assertTrue(cityQuests.isNotEmpty())
        assertTrue(cityQuests.all { it.cityId == cityId })
    }
}
