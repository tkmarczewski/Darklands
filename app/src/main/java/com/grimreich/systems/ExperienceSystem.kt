package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExperienceSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun addXp(hero: Hero, amount: Int): String {
        hero.xp += amount
        var leveledUp = false
        var levelsGained = 0

        // Fix: use while-loop to handle cascading level-ups
        // (e.g. gaining 500 XP at level 1 threshold 100 should give multiple levels)
        while (hero.xp >= hero.level * 100) {
            hero.xp -= hero.level * 100
            hero.level++
            hero.attributePoints += 2 // Grant 2 points per level
            leveledUp = true
            levelsGained++
        }

        gameRepository.persistCurrentState()
        return when {
            levelsGained > 1 -> "Awans x$levelsGained! ${hero.name} osiagnął poziom ${hero.level}."
            leveledUp -> "Awans! ${hero.name} osiagnął poziom ${hero.level}."
            else -> "Zdobyto $amount XP."
        }
    }
}
