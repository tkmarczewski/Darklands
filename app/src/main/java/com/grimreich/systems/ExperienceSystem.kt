package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExperienceSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun addXp(heroId: String, amount: Int): String {
        var levelsGained = 0
        var finalName = ""
        var finalLevel = 1
        var heroFound = false
        
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            heroFound = true
            finalName = hero.name
            hero.xp += amount
            
            while (hero.xp >= hero.level * 100) {
                hero.xp -= hero.level * 100
                hero.level++
                hero.attributePoints += 2
                levelsGained++
            }
            finalLevel = hero.level
        }
        
        if (!heroFound) return "Nie znaleziono bohatera."

        return when {
            levelsGained > 1 -> "Awans x$levelsGained! $finalName osiągnął poziom $finalLevel."
            levelsGained == 1 -> "Awans! $finalName osiągnął poziom $finalLevel."
            else -> "Zdobyto $amount XP dla $finalName."
        }
    }
}
