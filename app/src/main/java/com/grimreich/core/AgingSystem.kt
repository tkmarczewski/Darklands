package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgingSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun applyAging(hero: Hero) {
        if (hero.age > 40) {
            hero.agility = (hero.agility - 1).coerceAtLeast(1)
        }
        if (hero.age > 60) {
            hero.strength = (hero.strength - 1).coerceAtLeast(1)
            gameRepository.log("${hero.name} odczuwa ciężar lat na swoich barkach.")
        }
        if (hero.age > 80) {
            hero.intelligence = (hero.intelligence - 1).coerceAtLeast(1)
            hero.virtue = (hero.virtue - 1).coerceAtLeast(0)
        }
        gameRepository.persistCurrentState()
    }
}
