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
        val item = rollLoot(chance) ?: return ""
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
