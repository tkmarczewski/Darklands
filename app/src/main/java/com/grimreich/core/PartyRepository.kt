package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PartyRepository @Inject constructor(
    private val gameRepository: GameRepository
) {
    /**
     * FIX: Poprzedni setter mutowal currentState() bezposrednio:
     *   val state = gameRepository.currentState()
     *   state.activeHeroId = value
     *   gameRepository.persistCurrentState()
     *
     * To powodowalo trzy problemy:
     * 1. Mutacja live state pomijala deepCopy/normalizeState/synchronized w updateState.
     * 2. _gameState.value nie bylo aktualizowane - StateFlow NIE emitowalo nowej wartosci,
     *    wiec calkowite UI obserwujace gameRepository.gameState nie widzialo zmiany.
     * 3. persistCurrentState() bylo wywolywane bezposrednio zamiast przez updateState.
     *
     * Fix: uzyto updateState{} ktore:
     * - robi deepCopy, normalizeState, synchronized,
     * - ustawia _gameState.value = mutable (StateFlow emituje nowa wartosc),
     * - automatycznie persystuje (shouldPersist=true domyslnie).
     */
    var activeHeroId: String?
        get() = gameRepository.currentState().activeHeroId
        set(value) {
            gameRepository.updateState { state ->
                state.activeHeroId = value
            }
        }

    fun activeHero(): Hero? =
        activeHeroId?.let { id ->
            gameRepository.currentState().party.firstOrNull { it.id == id }
        }

    fun all(): List<Hero> = gameRepository.currentState().party
}

