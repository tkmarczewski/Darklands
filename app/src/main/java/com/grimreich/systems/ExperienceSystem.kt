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
        val leveledUp = if (hero.xp >= hero.level * 100) {
            hero.level++
            hero.attributePoints += 2 // Grant 2 points per level
            hero.xp = 0
            true
        } else false

        gameRepository.persistCurrentState()
        return if (leveledUp) "Awans! ${hero.name} osiągnął poziom ${hero.level}." else "Zdobyto $amount XP."
    }
}
