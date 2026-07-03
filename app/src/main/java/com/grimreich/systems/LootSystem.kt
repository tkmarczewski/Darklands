package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.ItemCatalogue
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class LootSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val itemCatalogue: ItemCatalogue
) {
    fun rollLoot(chance: Float): Item? {
        if (Random.nextFloat() > chance) return null
        return itemCatalogue.all().randomOrNull()
    }

    /**
     * Awards loot directly to the state. Use this inside updateState blocks.
     */
    fun awardLootDirect(state: GameState, chance: Float): String {
        // FIX (BUG-5): Handle empty catalogue explicitly
        if (itemCatalogue.all().isEmpty()) {
            state.logEntries.add("❌ BŁĄD: Katalog przedmiotów jest pusty!")
            return "Błąd: Brak dostępnych przedmiotów"
        }

        val item = rollLoot(chance) ?: run {
            state.logEntries.add("Przeszukano okolicę, ale nic nie znaleziono.")
            return ""
        }

        state.inventory.add(item.copy())
        state.logEntries.add("Zdobyto przedmiot: ${item.name}")
        return "Zdobyto przedmiot: ${item.name}"
    }

    fun awardLoot(chance: Float): String {
        var msg = ""
        gameRepository.updateState { state ->
            msg = awardLootDirect(state, chance)
        }
        return msg
    }
}
