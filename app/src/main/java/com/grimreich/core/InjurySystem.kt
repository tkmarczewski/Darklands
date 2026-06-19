package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InjurySystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun applyInjury(hero: Hero, damage: Int) {
        if (damage > hero.maxHp / 2) {
            hero.sanity -= 5
            gameRepository.log("${hero.name} odniósł ciężką ranę psychiczną.")
        }
        gameRepository.persistCurrentState()
    }
}
