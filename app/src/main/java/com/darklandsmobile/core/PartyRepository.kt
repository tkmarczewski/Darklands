package com.darklandsmobile.core

object PartyRepository {
    var activeHeroId: String?
        get() = GameRepository.state.activeHeroId
        set(value) { GameRepository.state.activeHeroId = value }

    fun activeHero(): Hero? =
        activeHeroId?.let { id ->
            GameRepository.state.party.firstOrNull { it.id == id }
        }

    fun all(): List<Hero> = GameRepository.state.party
}