package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PartyRepository @Inject constructor(
    private val gameRepository: GameRepository
) {
    var activeHeroId: String?
        get() = gameRepository.currentState().activeHeroId
        set(value) {
            val state = gameRepository.currentState()
            state.activeHeroId = value
            gameRepository.persistCurrentState()
        }

    fun activeHero(): Hero? =
        activeHeroId?.let { id ->
            gameRepository.currentState().party.firstOrNull { it.id == id }
        }

    fun all(): List<Hero> = gameRepository.currentState().party
}
