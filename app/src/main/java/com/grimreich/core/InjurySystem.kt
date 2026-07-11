package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InjurySystem @Inject constructor(
    private val gameRepository: GameRepository
) {

    fun applyInjury(heroId: String, damage: Int) {
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            if (hero.maxHp <= 0) return@updateState
            if (damage > hero.maxHp / 2) {
                hero.sanity = (hero.sanity - 5).coerceAtLeast(0)
                state.logEntries.add("${hero.name} odniósł ciężką ranę psychiczną.")
            }
        }
    }
}
