package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgingSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun applyAging(heroId: String) {
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            if (hero.age > 40) {
                hero.agility = (hero.agility - 1).coerceAtLeast(1)
            }
            if (hero.age > 60) {
                hero.strength = (hero.strength - 1).coerceAtLeast(1)
                state.logEntries.add("${hero.name} odczuwa ciężar lat na swoich barkach.")
            }
            if (hero.age > 80) {
                hero.intelligence = (hero.intelligence - 1).coerceAtLeast(1)
                hero.virtue = (hero.virtue - 1).coerceAtLeast(0)
            }
        }
    }
}
