package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChurchSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun pray(heroId: String): String {
        var msg = ""
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            hero.divineFavor = (hero.divineFavor + 10).coerceAtMost(150)
            hero.virtue += 1
            msg = "${hero.name} modli się żarliwie. (+10 Divine Favor, +1 Cnota)"
        }
        return msg.ifEmpty { "Brak bohatera." }
    }

    fun makeOffering(amount: Int): String {
        var msg = ""
        gameRepository.updateState { state ->
            if (state.gold < amount) {
                msg = "Brak wystarczającej ilości złota na ofiarę."
                return@updateState
            }

            state.gold -= amount
            val recovery = (amount / 10).coerceAtMost(20)
            state.world.globalStability = (state.world.globalStability + recovery).coerceAtMost(100)
            msg = "Złożono ofiarę w wysokości $amount zł. Stabilność świata wzrosła o $recovery."
        }
        return msg
    }

    fun cleanseRelic(heroId: String): String {
        var msg = ""
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            if (hero.corruption <= 0) {
                msg = "${hero.name} nie jest skażony mrokiem."
                return@updateState
            }

            val cost = hero.corruption * 5
            if (state.gold < cost) {
                msg = "Brak złota na ceremonię oczyszczenia (potrzeba $cost)."
                return@updateState
            }

            state.gold -= cost
            val reduction = hero.corruption / 2 + 5
            hero.corruption = (hero.corruption - reduction).coerceAtLeast(0)
            hero.sanity = (hero.sanity + 10).coerceAtMost(100)
            msg = "${hero.name} przeszedł rytuał oczyszczenia. Korupcja spadła o $reduction. Poczytalność wzrosła."
        }
        return msg.ifEmpty { "Brak bohatera." }
    }
}
