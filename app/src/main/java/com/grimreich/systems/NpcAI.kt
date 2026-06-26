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
        val intensity = gameRepository.currentState().world.echoIntensity
        if (intensity > 0.8f) {
            hero.sanity = (hero.sanity - 1).coerceAtLeast(0)
        }
        gameRepository.persistCurrentState()
    }
}
