package com.darklandsmobile.systems

import com.darklandsmobile.TestSupport
import com.darklandsmobile.core.GameRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuestSystemTest {

    @Before
    fun setUp() {
        TestSupport.resetRepoEmpty()
    }

    @Test
    fun `start adds quest to active and initializes progress`() {
        val msg = QuestSystem.start("forest_hermit")
        val q = GameRepository.state.quest
        assertTrue(q.activeQuests.contains("forest_hermit"))
        assertEquals(0, q.questProgress["forest_hermit"])
        assertTrue(msg.contains("Znajdz pustelnika w lesie"))
    }

    @Test
    fun `start is idempotent and reports already-active`() {
        QuestSystem.start("forest_hermit")
        val msg = QuestSystem.start("forest_hermit")
        assertTrue(msg.contains("juz aktywny"))
        assertEquals(1, GameRepository.state.quest.activeQuests.size)
    }

    @Test
    fun `advance increments progress and reports progress message`() {
        QuestSystem.start("bandit_camp")
        val msg = QuestSystem.advance("bandit_camp")
        assertEquals(1, GameRepository.state.quest.questProgress["bandit_camp"])
        assertTrue(msg.contains("postep 1/3"))
    }

    @Test
    fun `advance ignores unknown or inactive quest`() {
        val msg = QuestSystem.advance("ghost_quest")
        assertTrue(msg.contains("nie jest aktywny"))
    }

    @Test
    fun `advance completes quest when progress reaches 3`() {
        QuestSystem.start("lost_relic")
        QuestSystem.advance("lost_relic", 2)
        val msg = QuestSystem.advance("lost_relic", 1)

        val q = GameRepository.state.quest
        assertFalse(q.activeQuests.contains("lost_relic"))
        assertTrue(q.completedQuests.contains("lost_relic"))
        assertTrue(msg.contains("ukonczony"))
    }

    @Test
    fun `activeList reflects started quests`() {
        QuestSystem.start("forest_hermit")
        QuestSystem.start("bandit_camp")
        val list = QuestSystem.activeList()
        assertEquals(2, list.size)
        assertTrue(list.containsAll(listOf("forest_hermit", "bandit_camp")))
    }

    @Test
    fun `finalQuestSummary renders both active and completed sections`() {
        QuestSystem.start("forest_hermit")
        QuestSystem.advance("forest_hermit") // 1/3

        QuestSystem.start("bandit_camp")
        QuestSystem.advance("bandit_camp", 3) // completed

        val summary = QuestSystem.finalQuestSummary()
        assertTrue(summary.contains("Aktywne questy:"))
        assertTrue(summary.contains("Znajdz pustelnika w lesie (1/3)"))
        assertTrue(summary.contains("Ukonczone questy:"))
        assertTrue(summary.contains("Rozprosz oboz bandytow"))
    }

    @Test
    fun `finalQuestSummary handles empty lists with brak`() {
        val summary = QuestSystem.finalQuestSummary()
        assertTrue(summary.contains("Aktywne questy:\n  brak"))
        assertTrue(summary.contains("Ukonczone questy:\n  brak"))
    }
}
