package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RitualSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    private val REVIVAL_STABILITY_DRAIN = 15
    private val REVIVAL_CORRUPTION_GAIN = 20
    private val REVIVAL_SANITY_LOSS = 15

    /**
     * Sprawdza czy mozna wykonac resurrekcje dla danego bohatera.
     * Przyjmuje Hero jako parametr zamiast czytac currentState() wewnatrz,
     * co pozwala na uzycie wewnatrz updateState{} na tej samej kopii stanu.
     */
    fun canPerformResurrection(hero: Hero, globalStability: Int): Boolean {
        return hero.isDead && globalStability > REVIVAL_STABILITY_DRAIN
    }

    /**
     * Wykonuje resurrekcje bohatera.
     *
     * FIX 1 (TOCTOU): Poprzednia implementacja sprawdzala canPerformResurrection()
     * na currentState() PRZED updateState{}, a mutacje wykonywala na deepCopy.
     * Dwie rownoczesne korutyny mogly przejsc walidacje (oba widzia stabilnosc > drain),
     * a potem obie odjac REVIVAL_STABILITY_DRAIN, powodujac podwojne odejscie stability.
     * Fix: walidacja i mutacja na tej samej kopii wewnatrz updateState{}.
     *
     * FIX 2 (double-persist): updateState(shouldPersist=true) juz persystuje.
     * Dodatkowe persistCurrentState() po nim bylo redundantne.
     * Fix: usunieto zbedne persistCurrentState().
     */
    fun performResurrection(heroId: String): Boolean {
        var success = false
        gameRepository.updateState { s ->
            val h = s.party.find { it.id == heroId } ?: return@updateState
            // FIX: walidacja na tej samej kopii co mutacja - brak TOCTOU
            if (!canPerformResurrection(h, s.world.globalStability)) return@updateState

            s.world.globalStability = (s.world.globalStability - REVIVAL_STABILITY_DRAIN).coerceAtLeast(0)
            h.isDead = false
            h.hp = h.maxHp / 2
            h.corruption = (h.corruption + REVIVAL_CORRUPTION_GAIN).coerceAtMost(100)
            h.sanity = (h.sanity - REVIVAL_SANITY_LOSS).coerceAtLeast(0)
            s.logEntries.add("Rytual Echa powioddl sie. ${h.name} powraca z Otchlani, lecz fundamenty swiata drza.")
            success = true
        }
        // FIX: brak persistCurrentState() - updateState domyslnie persystuje (shouldPersist=true)
        return success
    }

    /**
     * Poswiecenie bohatera.
     *
     * FIX (double-persist): usunieto redundantne persistCurrentState() po updateState.
     */
    fun sacrificeHero(heroId: String) {
        gameRepository.updateState { s ->
            s.party.removeIf { it.id == heroId }
            s.logEntries.add("Pozwolono odejsc duszy bohatera. Niech Absolut go prowadzi.")
            if (s.activeHeroId == heroId) {
                s.activeHeroId = s.party.firstOrNull()?.id
            }
        }
        // FIX: brak persistCurrentState() - updateState domyslnie persystuje
    }
}
