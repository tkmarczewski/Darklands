package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class NpcAI @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun tickNpc(heroId: String) {
        gameRepository.updateState { state ->
            tickNpcDirect(state, heroId)
        }
    }

    fun tickNpcDirect(state: GameState, heroId: String) {
        val intensity = state.world.echoIntensity
        val day = state.world.day
    
        // Add determinism using day-based seed (OBS-07)
        val rng = Random(heroId.hashCode().toLong() + day.toLong())
    
        if (intensity > 0.8f && rng.nextFloat() < 0.3f) {
            val h = state.party.find { it.id == heroId } ?: return
            h.sanity = (h.sanity - 1).coerceAtLeast(0)
            state.logEntries.add("Cień podąża za ${h.name}... (-1 Sanity)")
        }
    }
}
