package com.grimreich.core.mutations

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.Hero
import com.grimreich.core.WorldState
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class MutationSystemTest {

    @Mock
    private lateinit var gameRepository: GameRepository

    private lateinit var mutationSystem: MutationSystem

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mutationSystem = MutationSystem(gameRepository)
    }

    @Test
    fun `checkForNewMutation applies mutation when stability is low`() {
        val hero = Hero(id = "test_hero", name = "Test Hero", age = 25, strength = 10)
        val stability = 10
        
        // We call it multiple times to ensure Random eventually triggers (since it's 15%)
        repeat(100) {
            mutationSystem.checkForNewMutation(hero, "region_1", stability)
        }
        
        assertTrue(hero.activeMutations.isNotEmpty())
    }

    @Test
    fun `applyMutation updates hero stats and world stability`() {
        val hero = Hero(id = "test_hero", name = "Test Hero", age = 25, strength = 10)
        val state = GameState(world = WorldState(globalStability = 100))
        
        `when`(gameRepository.updateState(any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[0] as (GameState) -> Unit
            transform(state)
        }

        // Simulating the trigger (we might need many attempts due to Random)
        var triggered = false
        repeat(1000) {
            if (!triggered && hero.activeMutations.isNotEmpty()) triggered = true
            mutationSystem.checkForNewMutation(hero, "region", 0)
        }
        
        assertTrue("Hero should have at least one mutation", hero.activeMutations.isNotEmpty())
        assertTrue("World stability should have decreased", state.world.globalStability < 100)
    }
}
