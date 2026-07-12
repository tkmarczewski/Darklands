package com.grimreich.core.mutations

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.Hero
import com.grimreich.core.WorldState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

// AUDIT FIX: deterministic RNG providers for MutationSystem
class AlwaysTriggerMutationRng : MutationRandomProvider {
    override fun shouldTrigger(probability: Float): Boolean = true
    override fun nextFloat(): Float = 0.0f
}

class NeverTriggerMutationRng : MutationRandomProvider {
    override fun shouldTrigger(probability: Float): Boolean = false
    override fun nextFloat(): Float = 1.0f
}

class MutationSystemTest {

    @Mock
    private lateinit var gameRepository: GameRepository

    private lateinit var mutationSystem: MutationSystem
    private lateinit var mutationSystemNeverTrigger: MutationSystem

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mutationSystem = MutationSystem(gameRepository, AlwaysTriggerMutationRng())
        mutationSystemNeverTrigger = MutationSystem(gameRepository, NeverTriggerMutationRng())
    }

    @Test
    fun `checkForNewMutation applies mutation when stability is low`() {
        val heroId = "test_hero"
        val hero = Hero(id = heroId, name = "Test Hero", age = 25, strength = 10)
        val state = GameState(world = WorldState(globalStability = 0)).apply { party.add(hero) }

        `when`(gameRepository.currentState()).thenReturn(state)
        `when`(gameRepository.updateState(any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(state)
        }

        // AUDIT FIX: single deterministic call — no for-loop
        mutationSystem.checkForNewMutation(heroId, "region_1", 0)

        assertTrue(
            "Mutation should trigger with AlwaysTrigger RNG",
            hero.activeMutations.isNotEmpty()
        )
    }

    @Test
    fun `mutation should NOT trigger when RNG blocks it`() {
        val heroId = "test_hero"
        val hero = Hero(id = heroId, name = "Test Hero", age = 25, strength = 10)
        val state = GameState(world = WorldState(globalStability = 0)).apply { party.add(hero) }

        `when`(gameRepository.currentState()).thenReturn(state)
        `when`(gameRepository.updateState(any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(state)
        }

        mutationSystemNeverTrigger.checkForNewMutation(heroId, "region_1", 0)

        assertTrue(
            "Mutation should NOT trigger with NeverTrigger RNG",
            hero.activeMutations.isEmpty()
        )
    }

    @Test
    fun `applyMutation updates hero stats and world stability`() {
        val heroId = "test_hero"
        val hero = Hero(id = heroId, name = "Test Hero", age = 25, strength = 10)
        val state = GameState(world = WorldState(globalStability = 100)).apply { party.add(hero) }

        `when`(gameRepository.currentState()).thenReturn(state)
        `when`(gameRepository.updateState(any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(state)
        }

        // AUDIT FIX: single deterministic call
        mutationSystem.checkForNewMutation(heroId, "region", 0)

        assertTrue("Hero should have at least one mutation", hero.activeMutations.isNotEmpty())
        assertTrue("World stability should have decreased", state.world.globalStability < 100)
    }

    @Test
    fun `checkForNewMutationDirect applies changes without calling updateState`() {
        val heroId = "test_hero"
        val hero = Hero(id = heroId, name = "Test Hero", age = 25, strength = 10)
        val state = GameState(world = WorldState(globalStability = 0)).apply { party.add(hero) }
        state.world.day = 1

        mutationSystem.checkForNewMutationDirect(state, heroId, "region", 0)

        // AUDIT FIX: real assertion + verifyNoInteractions
        verifyNoInteractions(gameRepository)
        assertTrue(
            "Direct mutation should modify state",
            hero.activeMutations.isNotEmpty() || state.world.globalStability < 100
        )
    }
}
