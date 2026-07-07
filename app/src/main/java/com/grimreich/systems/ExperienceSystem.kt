package com.grimreich.systems

import com.grimreich.core.GameState
import com.grimreich.core.Hero
import com.grimreich.core.GameRepository
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExperienceSystem @Inject constructor(
    private val gameRepository: Lazy<GameRepository>
) {
    fun addXp(heroId: String, amount: Int): String {
        var msg = ""
        gameRepository.get().updateState { state ->
            val hero = state.party.find { it.id == heroId }
            if (hero != null) {
                val levels = applyXpDirect(hero, amount)
                msg = if (levels > 0) {
                    "Awans! ${hero.name} osiągnął poziom ${hero.level}."
                } else {
                    "Zdobyto $amount XP dla ${hero.name}."
                }
            }
        }
        return msg
    }

    fun addPartyXp(amount: Int): List<String> {
        var msgs = listOf<String>()
        gameRepository.get().updateState { state ->
            msgs = addPartyXpDirect(state, amount)
        }
        return msgs
    }

    /**
     * Adds XP directly to state. Use this inside updateState blocks.
     */
    fun addPartyXpDirect(state: GameState, amount: Int): List<String> {
        val messages = mutableListOf<String>()
        state.party.filter { !it.isDead }.forEach { hero ->
            val levels = applyXpDirect(hero, amount)
            if (levels > 0) {
                messages.add("Awans! ${hero.name} osiągnął poziom ${hero.level}.")
            }
        }
        if (messages.isEmpty() && amount > 0) {
            messages.add("Zdobyto $amount XP dla drużyny.")
        }
        return messages
    }

    /**
     * Internal logic for applying XP and handling multiple levels.
     * @return Number of levels gained.
     */
    private fun applyXpDirect(hero: Hero, amount: Int): Int {
        if (amount <= 0) return 0
        hero.xp += amount
        var levelsGained = 0
        
        // FIX: Ensure level is at least 1
        if (hero.level < 1) hero.level = 1
        
        // Scaling XP requirement: level * 100
        while (hero.xp >= hero.level * 100) {
            hero.xp -= hero.level * 100
            hero.level++
            
            // FIX: Grant attribute points on level up (2 per level)
            hero.attributePoints += 2
            
            levelsGained++
            // Safety break
            if (levelsGained > 50) break
        }
        return levelsGained
    }
}
