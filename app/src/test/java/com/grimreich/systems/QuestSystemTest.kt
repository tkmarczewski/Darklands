package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuestSystemTest {

    @Before
    fun setup() {
        GameRepository.state = GameState()
        CityCatalogue.seedSprint1()
        QuestSystem.seedIntegratedContent(seed = 123)
    }

    @Test
    fun `availableForCity returns quests assigned to correct city`() {
        val cityId = "wybrzeze_polnocne"
        val quests = QuestSystem.availableForCity(cityId)
        assertTrue(quests.isNotEmpty())
        assertTrue(quests.all { it.cityId == cityId })
    }

    @Test
    fun `activate updates quest status to active`() {
        val quest = QuestSystem.all().first()
        val activated = QuestSystem.activate(quest.id)
        assertEquals(QuestStatus.AKTYWNE, activated.status)
    }

    @Test
    fun `complete updates quest status to completed`() {
        val quest = QuestSystem.all().first()
        val completed = QuestSystem.complete(quest.id)
        assertEquals(QuestStatus.UKONCZONE, completed.status)
    }
}
