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

class QuestChainIntegrityTest {

    @Mock
    private lateinit var gameRepository: GameRepository

    private lateinit var questEngine: QuestEngine

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        questEngine = QuestEngine(gameRepository)
    }

    @Test
    fun `verify quest chain sequence and locking logic`() {
        // 1. Arrange: Create a chain Q1 -> Q2
        val q1Id = "chain_q1"
        val q2Id = "chain_q2"
        
        val q1Def = QuestDefinition(q1Id, "Q1", "...", 10, listOf(QuestStep("S1", StepType.DIALOGUE, "T1")), "C1", "N1")
        val q2Def = QuestDefinition(q2Id, "Q2", "...", 20, listOf(QuestStep("S2", StepType.DIALOGUE, "T2")), "C1", "N1", prerequisiteQuestId = q1Id)
        
        questEngine.register(q1Def)
        questEngine.register(q2Def)

        val state = GameState()
        `when`(gameRepository.currentState()).thenReturn(state)
        `when`(gameRepository.updateState(any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[0] as (GameState) -> Unit
            transform(state)
        }

        // 2. Initial State: Q1 AVAILABLE, Q2 LOCKED
        assertEquals("Q1 should be available", QuestStatus.AVAILABLE, questEngine.getStatus(q1Id))
        assertEquals("Q2 should be locked", QuestStatus.LOCKED, questEngine.getStatus(q2Id))

        // 3. Activate and Complete Q1
        questEngine.activateQuest(q1Id)
        questEngine.advanceStep(q1Id)
        questEngine.completeQuest(q1Id)

        assertEquals("Q1 should be completed", QuestStatus.COMPLETED, questEngine.getStatus(q1Id))

        // 4. Q2 should now be AVAILABLE
        assertEquals("Q2 should now be available", QuestStatus.AVAILABLE, questEngine.getStatus(q2Id))

        // 5. Verify activation of Q2
        questEngine.activateQuest(q2Id)
        assertEquals("Q2 should be active", QuestStatus.ACTIVE, questEngine.getStatus(q2Id))
    }
}
