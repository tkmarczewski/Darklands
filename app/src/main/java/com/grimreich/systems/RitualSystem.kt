package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RitualSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    private val REVIVAL_STABILITY_DRAIN = 15
    private val REVIVAL_CORRUPTION_GAIN = 20
    private val REVIVAL_SANITY_LOSS = 15

    fun canPerformResurrection(hero: Hero): Boolean {
        val state = gameRepository.currentState()
        return hero.isDead && state.world.globalStability > REVIVAL_STABILITY_DRAIN
    }

    fun performResurrection(heroId: String): Boolean {
        val state = gameRepository.currentState()
        val hero = state.party.find { it.id == heroId } ?: return false
        
        if (!canPerformResurrection(hero)) return false

        gameRepository.updateState { s ->
            val h = s.party.find { it.id == heroId } ?: return@updateState
            s.world.globalStability -= REVIVAL_STABILITY_DRAIN
            
            h.isDead = false
            h.hp = h.maxHp / 2
            h.corruption += REVIVAL_CORRUPTION_GAIN
            h.sanity -= REVIVAL_SANITY_LOSS
            
            s.logEntries.add("Rytuał Echa powiódł się. ${h.name} powraca z Otchłani, lecz fundamenty świata drżą.")
        }
        gameRepository.persistCurrentState()
        return true
    }

    fun sacrificeHero(heroId: String) {
        gameRepository.updateState { s ->
            s.party.removeIf { it.id == heroId }
            s.logEntries.add("Pozwolono odejść duszy bohatera. Niech Absolut go prowadzi.")
            if (s.activeHeroId == heroId) {
                s.activeHeroId = s.party.firstOrNull()?.id
            }
        }
        gameRepository.persistCurrentState()
    }
}
