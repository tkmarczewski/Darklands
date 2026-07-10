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
            state.prayer.faith += 5
            hero.sanity = (hero.sanity + 10).coerceAtMost(100)
            msg = "${hero.name} oddaje się modlitwie. Spokój spływa na jego duszę (+10 Sanity)."
            state.logEntries.add(msg)
        }
        return msg
    }

    fun makeOffering(goldAmount: Int): String {
        var msg = ""
        gameRepository.updateState { state ->
            if (state.gold >= goldAmount) {
                state.gold -= goldAmount
                state.prayer.faith += goldAmount / 2
                msg = "Złożono ofiarę w wysokości $goldAmount zł. Bogowie patrzą łaskawiej (+${goldAmount/2} Faith)."
                state.logEntries.add(msg)
            } else {
                msg = "Nie masz wystarczająco dużo złota."
            }
        }
        return msg
    }

    fun performResurrection(heroId: String, negotiated: Boolean = false): String {
        var msg = ""
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            if (!hero.isDead) {
                msg = "${hero.name} wciąż żyje."
                return@updateState
            }

            val corpseId = "corpse_${hero.id}"
            val corpseItem = state.inventory.find { it.instanceId == corpseId }

            if (corpseItem == null) {
                msg = "Nie masz przy sobie ciała tego bohatera."
                return@updateState
            }

            val baseGold = 300
            val baseFaith = 100
            val stabilityPenalty = if (negotiated) 30 else 15
            val actualGold = if (negotiated) 150 else baseGold

            if (state.prayer.faith < baseFaith || state.gold < actualGold) {
                msg = "Rytuał wymaga większej ofiary (${baseFaith} Wiary i ${actualGold} Złota)."
                return@updateState
            }

            state.prayer.faith -= baseFaith
            state.gold -= actualGold
            state.inventory.remove(corpseItem)

            // Wskrzeszenie
            hero.isDead = false
            hero.hp = 1
            hero.sanity = 5
            hero.corruption += 30
            
            // Konsekwencja: Cień Towarzysza
            state.logEntries.add("UWAGA: Odrodzenie ${hero.name} powołało do życia jego Cień.")
            state.logEntries.add("Cień będzie nawiedzał Waszą sesję, karmiąc się stabilnością paradygmatu.")
            
            // SKRYTA CENA: Spadek stabilności świata
            state.world.globalStability -= stabilityPenalty
            
            msg = "Rytuał dobiegł końca. ${hero.name} otwiera oczy, ale jego spojrzenie jest puste..."
            state.logEntries.add(msg)
            state.logEntries.add("Kapłan szepcze: 'Byłem pewien, że wiesz jaka jest cena... a mimo to przyszedłeś.'")
            if (negotiated) {
                state.logEntries.add("Głos w Twojej głowie: 'Targowałeś się o życie przyjaciela. Paradygmat zapamięta Twoje skąpstwo.'")
            }
        }
        return msg
    }

    fun cleanseRelic(itemId: String): String {
        var msg = ""
        gameRepository.updateState { state ->
            val item = state.inventory.find { it.instanceId == itemId } ?: return@updateState
            state.inventory.remove(item)
            state.world.globalStability += 5
            msg = "Oczyszczono relikwię: ${item.name}. Stabilność świata wzrosła."
            state.logEntries.add(msg)
        }
        return msg
    }
}
