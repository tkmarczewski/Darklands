package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.mutations.MutationSystem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MutationEngine @Inject constructor(
    private val gameRepository: GameRepository,
    private val mutationSystem: MutationSystem,
    private val chronicleSystem: ChronicleSystem
) {
    /**
     * High-level orchestration of mutation effects across the world and party.
     * Refactored to use a single updateState block for performance.
     */
    fun processMutations() {
        // BUG FIX: Extract side-effects from updateState to prevent nested transactions
        val currentState = gameRepository.currentState()
        if (currentState.world.echoIntensity > 0.6f) {
            chronicleSystem.record("Miejscowa fauna zaczyna mutować pod wpływem Echa.", 3)
        }

        gameRepository.updateState { state ->
            // Party-level effects
            state.party.forEach { hero ->
                mutationSystem.checkForNewMutationDirect(
                    state = state,
                    heroId = hero.id,
                    regionId = state.world.locationId,
                    currentStability = state.world.globalStability
                )
            }
        }
    }
}
