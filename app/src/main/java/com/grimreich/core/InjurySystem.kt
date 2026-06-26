package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InjurySystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    companion object {
        // BUG-R4-01: Sanity clamp cap
        private const val SANITY_CAP = 100
    }

    fun applyInjury(hero: Hero, damage: Int) {
        // BUG-R4-02: Guard against maxHp <= 0 (avoid division by zero and always-true condition)
        if (hero.maxHp <= 0) return 

        if (damage > hero.maxHp / 2) {
            // BUG-R3-05: Sanity clamp — previously: hero.sanity -= 5 (no clamp!)
            hero.sanity = (hero.sanity - 5).coerceIn(0, SANITY_CAP)
            gameRepository.log("${hero.name} odniósł ciężką ranę psychiczną.")
        }
        gameRepository.persistCurrentState()
    }
}
