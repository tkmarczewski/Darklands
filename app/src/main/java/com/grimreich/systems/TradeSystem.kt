package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.Item
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TradeSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val economySystem: EconomySystem
) {
    /**
     * Kupuje przedmiot od sprzedawcy.
     *
     * FIX: Poprzednia implementacja czytala currentState() i mutowala live state
     * bezposrednio (state.gold -= price, state.inventory.add(item)), omijajac
     * updateState{} i jego deepCopy/normalizeState/synchronized.
     * Naprawiono: cala transakcja odbywa sie wewnatrz updateState{}.
     *
     * FIX: Poprzednia walidacja "if (state.gold < price) return" byla PRZED
     * updateState{}, wiec sprawdzala stale currentState(), a nie mutowalny snapshot.
     * Teraz walidacja jest WEWNATRZ updateState{}, dziala na tej samej kopii co mutacja.
     */
    fun buyGood(cityId: String, item: Item): String {
        val price = economySystem.priceInCity(cityId, item.value)
        var result = ""
        gameRepository.updateState { state ->
            if (state.gold < price) {
                result = "Brak zlota! (potrzeba $price, masz ${state.gold})"
                return@updateState
            }
            state.gold -= price
            state.inventory.add(item.copy())
            state.logEntries.add("Kupiono ${item.name} za $price zl.")
            result = "Kupiono ${item.name} za $price zl."
        }
        return result
    }

    /**
     * Sprzedaje przedmiot.
     *
     * FIX: Poprzednia implementacja tylko liczyla cene sprzedazy, ale nigdy nie
     * usuwala itemu z ekwipunku ani nie dodawala zlota do stanu gracza.
     * Naprawiono: transakcja sprzedazy jest kompletna - item usuniety, gold dodany.
     */
    fun sellItem(item: Item, cityId: String): String {
        val sellPrice = economySystem.calculateSellPrice(item)
        var result = ""
        gameRepository.updateState { state ->
            val found = state.inventory.find { it.id == item.id }
            if (found == null) {
                result = "Nie masz tego przedmiotu w ekwipunku."
                return@updateState
            }
            state.inventory.remove(found)
            state.gold += sellPrice
            state.logEntries.add("Sprzedano ${item.name} za $sellPrice zl.")
            result = "Sprzedano ${item.name} za $sellPrice zl."
        }
        return result
    }
}
