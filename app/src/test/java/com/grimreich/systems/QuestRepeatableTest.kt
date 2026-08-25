package com.grimreich.systems

import com.grimreich.core.GameState
import com.grimreich.core.QuestStatus
import com.grimreich.core.StepType
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuestRepeatableTest {
    private lateinit var questEngine: QuestEngine
    private val state = GameState()

    @Before
    fun setup() {
        val expSystem = mock<ExperienceSystem>()
        whenever(expSystem.addPartyXpDirect(any(), any())).thenReturn(emptyList())
        
        questEngine = QuestEngine(
            context = mock(),
            gameRepositoryProvider = { mock() },
            experienceSystemProvider = { expSystem }
        )
    }

    @Test
    fun `repeatable quest should be available again after completion`() {
        val repeatableQuest = QuestDefinition(
            id = "q_repeat",
            title = "Powtarzalne",
            description = "Opis",
            rewardGold = 50,
            steps = listOf(QuestStep("Zabij", StepType.kill, "any")),
            cityId = "c1",
            originNpcId = "n1",
            repeatable = true
        )
        questEngine.register(repeatableQuest)

        // 1. Activate
        questEngine.activateQuestDirect(state, "q_repeat")
        assertEquals(QuestStatus.active, questEngine.getStatus("q_repeat", state))

        // 2. Complete steps
        questEngine.advanceStepDirect(state, "q_repeat")
        assertEquals(QuestStatus.objective_met, questEngine.getStatus("q_repeat", state))

        // 3. Complete quest
        state.world.locationId = "c1" // Must be in right city
        questEngine.completeQuestDirect(state, "q_repeat")
        
        // 4. Verify it's NOT in completedQuestIds and IS available again
        assertFalse(state.quest.completedQuestIds.contains("q_repeat"), "Repeatable quest should not be in global completed list")
        assertEquals(QuestStatus.available, questEngine.getStatus("q_repeat", state), "Repeatable quest should be available again immediately")
    }

    @Test
    fun `non-repeatable quest should stay completed`() {
        val normalQuest = QuestDefinition(
            id = "q_normal",
            title = "Normalne",
            description = "Opis",
            rewardGold = 50,
            steps = listOf(QuestStep("Zabij", StepType.kill, "any")),
            cityId = "c1",
            originNpcId = "n1",
            repeatable = false
        )
        questEngine.register(normalQuest)

        // 1. Activate and complete
        questEngine.activateQuestDirect(state, "q_normal")
        questEngine.advanceStepDirect(state, "q_normal")
        state.world.locationId = "c1"
        questEngine.completeQuestDirect(state, "q_normal")
        
        // 2. Verify status
        assertEquals(QuestStatus.completed, questEngine.getStatus("q_normal", state))
        assertTrue(state.quest.completedQuestIds.contains("q_normal"))
        
        // 3. Try to reactivate
        questEngine.activateQuestDirect(state, "q_normal")
        assertFalse(state.quest.activeQuestIds.contains("q_normal"), "Should not reactivate non-repeatable quest")
    }
}
