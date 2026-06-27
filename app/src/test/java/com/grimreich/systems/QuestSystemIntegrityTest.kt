package com.grimreich.systems

import com.grimreich.core.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class QuestSystemIntegrityTest {

    @Mock
    private lateinit var gameRepository: GameRepository

    private lateinit var questEngine: QuestEngine

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        questEngine = QuestEngine(gameRepository)
    }

    @Test
    fun `verify full quest lifecycle from activation to completion`() {
        // 1. Arrange: Register a multi-step quest
        val questId = "q_test_lifecycle"
        val definition = QuestDefinition(
            id = questId,
            title = "Test Quest",
            description = "A quest to verify lifecycle",
            rewardGold = 100,
            cityId = "test_city",
            originNpcId = "test_npc",
            steps = listOf(
                QuestStep("Step 1", StepType.INVESTIGATION, "target_1"),
                QuestStep("Step 2", StepType.COMBAT, "target_2")
            )
        )
        questEngine.register(definition)

        val state = GameState(gold = 50)
        `when`(gameRepository.currentState()).thenReturn(state)
        `when`(gameRepository.updateState(any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[0] as (GameState) -> Unit
            transform(state)
        }

        // 2. Act: Activate
        questEngine.activateQuest(questId)
        
        // Assert Activation
        assertEquals("Quest should be ACTIVE", QuestStatus.ACTIVE, questEngine.getStatus(questId))
        assertTrue("Quest ID should be in active list", state.quest.activeQuestIds.contains(questId))

        // 3. Act: Advance to Step 2
        questEngine.advanceStep(questId)
        assertEquals("Should be at index 1", 1, state.quest.progress[questId]?.currentStepIndex)
        assertEquals("Status should still be ACTIVE", QuestStatus.ACTIVE, questEngine.getStatus(questId))

        // 4. Act: Advance to Objective Met
        questEngine.advanceStep(questId)
        assertEquals("Status should be OBJECTIVE_MET", QuestStatus.OBJECTIVE_MET, questEngine.getStatus(questId))

        // 5. Act: Complete
        questEngine.completeQuest(questId)
        
        // Assert Completion
        assertEquals("Status should be COMPLETED", QuestStatus.COMPLETED, questEngine.getStatus(questId))
        assertEquals("Gold should be awarded (50 + 100)", 150, state.gold)
        assertTrue("Quest should be in completed list", state.quest.completedQuestIds.contains(questId))
        assertTrue("Quest should NOT be in active list", !state.quest.activeQuestIds.contains(questId))
    }

    @Test
    fun `verify quest progress mapping through DTO cycle`() {
        val questId = "q_persistence_test"
        val originalProgress = QuestProgress(
            questId = questId,
            status = QuestStatus.ACTIVE,
            currentStepIndex = 1,
            variables = mutableMapOf("evidence_found" to 1)
        )
        
        val state = GameState()
        state.quest.progress[questId] = originalProgress
        state.quest.activeQuestIds.add(questId)

        // Map to DTO and back (Simulated persistence cycle)
        val dto = state.toDto()
        val restoredState = dto.toDomain()

        val restoredProgress = restoredState.quest.progress[questId]
        
        assertEquals("Quest ID must match", questId, restoredProgress?.questId)
        assertEquals("Status must match", QuestStatus.ACTIVE, restoredProgress?.status)
        assertEquals("Step index must match", 1, restoredProgress?.currentStepIndex)
        assertEquals("Variable must match", 1, restoredProgress?.variables?.get("evidence_found"))
        assertTrue("Active list must match", restoredState.quest.activeQuestIds.contains(questId))
    }
}
