package com.grimreich.systems

import android.content.Context
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.core.EchoSystem
import com.grimreich.world.HeroPool
import com.grimreich.systems.WorldStabilitySystem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RitualSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val echoSystem: EchoSystem,
    private val heroPool: HeroPool,
    private val worldStabilitySystem: WorldStabilitySystem,
    @ApplicationContext private val context: Context
) {
    companion object {
        const val REVIVAL_STABILITY_DRAIN = 15
        const val REVIVAL_CORRUPTION_GAIN = 10
        const val REVIVAL_SANITY_LOSS = 20
    }

    fun canPerformResurrection(hero: Hero, gold: Int): Boolean {
        return hero.isDead && gold >= 50
    }

    fun performResurrection(heroId: String): Boolean {
        var success = false
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId }
            if (hero != null && hero.isDead && state.gold >= 50) {
                state.gold -= 50
                hero.isDead = false
                hero.hp = hero.maxHp / 2
                hero.corruption += REVIVAL_CORRUPTION_GAIN
                hero.sanity -= REVIVAL_SANITY_LOSS
                hero.normalize()
                
                // STABILITY FIX: Use system for consistent range validation
                worldStabilitySystem.changeStabilityDirect(state, -REVIVAL_STABILITY_DRAIN, "Wskrzeszenie")

                state.logEntries.add("Wskrzeszono ${hero.name}. Rzeczywistość drży...")
                success = true
            }
        }
        return success
    }

    /**
     * Permanent death choice. Records the hero as an Echo and replaces them.
     */
    fun sacrificeHero(heroId: String) {
        gameRepository.updateState { state ->
            val heroIndex = state.party.indexOfFirst { it.id == heroId }
            if (heroIndex != -1) {
                val hero = state.party[heroIndex]
                
                // Record in EchoSystem
                echoSystem.recordHero(hero, context)
                
                // Remove from party
                state.party.removeAt(heroIndex)
                
                state.logEntries.add("Pozwolono odejść ${hero.name}. Ich dusza błąka się w pustce.")
                
                // Add replacement if party is empty or too small
                if (state.party.isEmpty() || state.party.size < 3) {
                    val newHero = heroPool.generateHero()
                    state.party.add(newHero)
                    state.logEntries.add("Nowy wędrowiec dołączył do drużyny: ${newHero.name}")
                }
                
                // Ensure activeHeroId is valid
                val currentActive = state.party.find { it.id == state.activeHeroId }
                if (currentActive == null || currentActive.isDead) {
                    state.activeHeroId = state.party.firstOrNull { !it.isDead }?.id
                }
                
                state.world.globalStability += 5 // Peace brings some stability
            }
        }
    }
}
