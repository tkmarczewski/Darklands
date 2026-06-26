package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InjurySystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun applyInjury(hero: Hero, damage: Int) {
        if (hero.maxHp <= 0) return // Guard maxHp

        if (damage > hero.maxHp / 2) {
            hero.sanity = (hero.sanity - 5).coerceAtLeast(0) // Sanity clamp
            gameRepository.log("${hero.name} odniósł ciężką ranę psychiczną.")
        }
        gameRepository.persistCurrentState()
    }
}
