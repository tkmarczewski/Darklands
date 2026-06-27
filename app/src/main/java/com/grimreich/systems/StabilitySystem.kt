package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StabilitySystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun updateStability(delta: Int) {
        gameRepository.updateState { state ->
            val current = state.world.globalStability
            val next = (current + delta).coerceIn(0, 100)
            state.world.globalStability = next
            
            if (delta < 0) state.logEntries.add("Stabilność rzeczywistości słabnie...")
            
            // CULMINATION: Zero Stability Logic
            if (next == 0 && current > 0) {
                triggerCollapse(state)
            }
        }
    }

    private fun triggerCollapse(state: GameState) {
        state.logEntries.add("!!! PARADYGMAT ULEGŁ CAŁKOWITEMU ROZPADOWI !!!")
        state.world.echoIntensity = 1.0f
        state.world.collapseProgress = 1.0f
        
        // At 0 stability, heroes take sanity damage every tick
        state.party.forEach { hero ->
            hero.sanity = (hero.sanity - 20).coerceAtLeast(0)
            if (hero.sanity == 0) {
                hero.hp = (hero.hp - 5).coerceAtLeast(0)
            }
        }
        
        // Instanced NPC name changes (already partially in generator, but here we can force it)
        state.knownNpcs.forEach { (_, list) ->
            // In a real system we might mutate existing NPCs here
        }
    }

    fun getStabilityModifier(): Float {
        val stability = gameRepository.currentState().world.globalStability
        return when {
            stability >= 90 -> 1.0f
            stability >= 50 -> 0.8f
            stability >= 20 -> 0.5f
            else -> 0.2f
        }
    }
}
