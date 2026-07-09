package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.LootTable
import com.grimreich.core.CombatRandomProvider
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.ItemCatalogue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LootSystem @Inject constructor(
    private val gameRepository: GameRepository,
    val itemCatalogue: ItemCatalogue,
    private val random: CombatRandomProvider
) {
    fun rollLoot(chance: Float): Item? {
        if (random.nextFloat() > chance) return null
        return itemCatalogue.getRandomItem()
    }

    fun awardLootDirect(state: GameState, chance: Float): String {
        val item = rollLoot(chance)
        return if (item != null) {
            state.inventory.add(item.copy())
            "Znaleziono przedmiot: ${item.name}"
        } else {
            "Nie znaleziono nic wartościowego."
        }
    }

    fun awardSpecificItemDirect(state: GameState, itemId: String): Boolean {
        val item = itemCatalogue.get(itemId)
        return if (item != null) {
            state.inventory.add(item.copy())
            true
        } else {
            false
        }
    }

    fun awardLootFromTableDirect(state: GameState, table: LootTable): List<String> {
        val messages = mutableListOf<String>()
        if (table.goldMax > 0) {
            val gold = (table.goldMin..table.goldMax).random()
            if (gold > 0) {
                state.gold += gold
                messages.add("Zdobyto $gold złota.")
            }
        }
        table.itemChances.forEach { (itemId, chance) ->
            if (random.nextFloat() <= chance) {
                val item = itemCatalogue.get(itemId)
                if (item != null) {
                    state.inventory.add(item.copy())
                    messages.add("Zdobyto przedmiot: ${item.name}")
                }
            }
        }
        return messages
    }
}
