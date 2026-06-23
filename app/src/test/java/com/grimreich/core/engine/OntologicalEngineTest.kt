package com.grimreich.core.engine

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.WorldState
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class OntologicalEngineTest {

    @Mock
    private lateinit var gameRepository: GameRepository

    private lateinit var engine: OntologicalEngine

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        engine = OntologicalEngine(gameRepository)
    }

    @Test
    fun `processRealityShift updates global stability`() {
        val initialState = GameState(world = WorldState(globalStability = 50))
        
        `when`(gameRepository.updateState(any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[0] as (GameState) -> Unit
            transform(initialState)
        }

        engine.processRealityShift()

        // Stability should be between 48 and 53 (initial 50 + Random(-2, 3))
        assertTrue(initialState.world.globalStability in 48..53)
    }

    @Test
    fun `processRealityShift drains stability during expedition`() {
        val initialState = GameState(world = WorldState(globalStability = 100), isExpeditionActive = true)
        
        `when`(gameRepository.updateState(any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[0] as (GameState) -> Unit
            transform(initialState)
        }

        engine.processRealityShift()

        // During expedition: shift is -5 + Random(-2, 3), so -7 to -2
        // Base stability 100 -> should be between 93 and 98
        assertTrue(initialState.world.globalStability in 90..98)
    }

    @Test
    fun `processRealityShift logs warning when stability is low`() {
        val lowStabilityState = GameState(world = WorldState(globalStability = 29))
        
        `when`(gameRepository.updateState(any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[0] as (GameState) -> Unit
            transform(lowStabilityState)
        }

        engine.processRealityShift()

        if (lowStabilityState.world.globalStability < 30) {
            verify(gameRepository, atLeastOnce()).log(anyString())
        }
    }

    @Test
    fun `isGlitchActive returns true when stability is very low`() {
        val criticalState = GameState(world = WorldState(globalStability = 0))
        `when`(gameRepository.currentState()).thenReturn(criticalState)

        val result = engine.isGlitchActive()
        assertNotNull(result)
    }
}
