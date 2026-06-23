package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChurchSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun pray(hero: Hero): String {
        hero.divineFavor = (hero.divineFavor + 10).coerceAtMost(150)
        hero.virtue += 1
        gameRepository.persistCurrentState()
        return "${hero.name} modli się żarliwie. (+10 Divine Favor, +1 Cnota)"
    }

    fun makeOffering(amount: Int): String {
        val state = gameRepository.currentState()
        if (state.gold < amount) return "Brak wystarczającej ilości złota na ofiarę."

        state.gold -= amount
        // Stability recovery: 1 stability for every 10 gold, max 20 per offering
        val recovery = (amount / 10).coerceAtMost(20)
        state.world.globalStability = (state.world.globalStability + recovery).coerceAtMost(100)

        gameRepository.persistCurrentState()
        return "Złożono ofiarę w wysokości $amount zł. Stabilność świata wzrosła o $recovery."
    }

    fun cleanseRelic(hero: Hero): String {
        if (hero.corruption <= 0) return "${hero.name} nie jest skażony mrokiem."

        val cost = hero.corruption * 5
        val state = gameRepository.currentState()
        if (state.gold < cost) return "Brak złota na ceremonię oczyszczenia (potrzeba $cost)."

        state.gold -= cost
        val reduction = hero.corruption / 2 + 5
        hero.corruption = (hero.corruption - reduction).coerceAtLeast(0)
        hero.sanity = (hero.sanity + 10).coerceAtMost(100)

        gameRepository.persistCurrentState()
        return "${hero.name} przeszedł rytuał oczyszczenia. Korupcja spadła o $reduction. Poczytalność wzrosła."
    }
}
