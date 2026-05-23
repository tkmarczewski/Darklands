package com.darklandsmobile.core

object PartyRepository {
    var activeHeroId: String
        get() = GameRepository.state.activeHeroId
        set(value) { GameRepository.state.activeHeroId = value }

    fun activeHero() = GameRepository.state.party.firstOrNull { it.id == activeHeroId }
    fun all() = GameRepository.state.party
}
