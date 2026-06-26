package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NpcAI @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun tickNpc(hero: Hero) {
        val state = gameRepository.currentState()
        val intensity = state.world.echoIntensity
        val day = state.world.day
        
        // Add determinism using day-based seed (OBS-07)
        val rng = kotlin.random.Random(hero.id.hashCode().toLong() + day.toLong())
        
        if (intensity > 0.8f && rng.nextFloat() < 0.3f) {
            hero.sanity = (hero.sanity - 1).coerceAtLeast(0)
            gameRepository.log("Cień podąża za ${hero.name}... (-1 Sanity)")
        }
        gameRepository.persistCurrentState()
    }
}
