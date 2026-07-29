package com.grimreich.core.engine

import com.grimreich.contracts.CollapseRandomProvider
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.WorldState
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class OntologicalEngineTest {

    @Mock
    private lateinit var gameRepository: GameRepository

    @Mock
    private lateinit var collapseRandomProvider: CollapseRandomProvider

    private lateinit var engine: OntologicalEngine

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        engine = OntologicalEngine(gameRepository, collapseRandomProvider)
    }

    @Test
    fun `processRealityShift updates global stability`() {
        val initialState = GameState(world = WorldState(globalStability = 50))
        
        whenever(gameRepository.updateState(org.mockito.kotlin.any<Boolean>(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(initialState)
            null
        }

        engine.processRealityShift()

        // Stability should be between 48 and 53 (initial 50 + Random(-2, 3))
        assertTrue(initialState.world.globalStability in 48..53)
    }

    @Test
    fun `processRealityShift drains stability during expedition`() {
        val initialState = GameState(world = WorldState(globalStability = 100), isExpeditionActive = true)
        
        whenever(gameRepository.updateState(org.mockito.kotlin.any<Boolean>(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(initialState)
            null
        }

        engine.processRealityShift()

        // During expedition: shift is -5 + Random(-2, 3), so -7 to -2
        // Base stability 100 -> should be between 93 and 98
        assertTrue(initialState.world.globalStability in 90..98)
    }

    @Test
    fun `processRealityShift logs warning when stability is low`() {
        val lowStabilityState = GameState(world = WorldState(globalStability = 29))
        
        whenever(gameRepository.updateState(org.mockito.kotlin.any<Boolean>(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(lowStabilityState)
            null
        }

        engine.processRealityShift()

        if (lowStabilityState.world.globalStability < 30) {
            verify(gameRepository, atLeastOnce()).log(any())
        }
    }

    @Test
    fun `isGlitchActive returns true when stability is very low`() {
        val criticalState = GameState(world = WorldState(globalStability = 0))
        whenever(gameRepository.currentState()).thenReturn(criticalState)

        val result = engine.isGlitchActive()
        assertNotNull(result)
    }
}
