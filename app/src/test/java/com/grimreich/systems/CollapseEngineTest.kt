package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.WorldState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CollapseEngineTest {

    private lateinit var gameRepository: GameRepository
    private lateinit var worldStabilitySystem: WorldStabilitySystem
    private lateinit var collapseEngine: CollapseEngine
    private lateinit var state: GameState

    @Before
    fun setup() {
        state = GameState()
        gameRepository = mock()
        whenever(gameRepository.currentState()).thenReturn(state)
        // updateState is hard to mock with whenever due to lambda, but we can use real impl if we have a simple repo mock or just test the logic
        
        worldStabilitySystem = WorldStabilitySystem(gameRepository)
        collapseEngine = CollapseEngine(gameRepository, worldStabilitySystem)
    }

    @Test
    fun processCollapseEvent_shouldAdvanceProgress() {
        val initialProgress = state.world.collapseProgress
        
        whenever(gameRepository.updateState(org.mockito.kotlin.any(), org.mockito.kotlin.any())).thenAnswer { invocation ->
            val transform = invocation.getArgument<(GameState) -> Unit>(1)
            transform(state)
            null
        }

        collapseEngine.processCollapseEvent(CollapseEvent.DayEnded)
        
        assertTrue("Progress should increase after DayEnded", state.world.collapseProgress > initialProgress)
        assertEquals(0.05f, state.world.collapseProgress, 0.001f)
    }

    @Test
    fun thresholdEffects_shouldBeIdempotent() {
        state.world.collapseProgress = 0.59f
        state.world.collapseScenarioId = "BLOOD_RUIN"
        
        whenever(gameRepository.updateState(org.mockito.kotlin.any(), org.mockito.kotlin.any())).thenAnswer { invocation ->
            val transform = invocation.getArgument<(GameState) -> Unit>(1)
            transform(state)
            null
        }

        // Crossing 60% threshold
        collapseEngine.processCollapseEvent(CollapseEvent.DayEnded) // +0.05 -> 0.64
        
        assertTrue("Threshold 0.6 should be reached", state.world.reachedThresholds.contains(0.6f))
        val logsAt60 = state.logEntries.count { it.contains("Przekroczono próg upadku 60%") }
        assertEquals(1, logsAt60)

        // Calling again while still above 60% but below 75%
        collapseEngine.processCollapseEvent(CollapseEvent.DayEnded) // +0.05 -> 0.69
        val logsAt60Again = state.logEntries.count { it.contains("Przekroczono próg upadku 60%") }
        assertEquals("Threshold effect should not fire twice", 1, logsAt60Again)
    }
}
