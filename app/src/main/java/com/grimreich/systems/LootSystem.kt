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
        return itemCatalogue.getRandomItemInstance()
    }

    fun awardLootDirect(state: GameState, chance: Float): String {
        val item = rollLoot(chance)
        return if (item != null) {
            state.inventory.add(item)
            "Znaleziono przedmiot: ${item.name}"
        } else {
            "Nie znaleziono nic wartościowego."
        }
    }

    fun awardSpecificItemDirect(state: GameState, templateId: String): Boolean {
        val item = itemCatalogue.createInstance(templateId)
        return if (item != null) {
            state.inventory.add(item)
            true
        } else {
            false
        }
    }

    fun awardLootFromTableDirect(state: GameState, table: LootTable): List<String> {
        val messages = mutableListOf<String>()
        if (table.goldMax > 0) {
            val gold = random.nextInt(table.goldMin, table.goldMax + 1)
            if (gold > 0) {
                state.gold += gold
                messages.add("Zdobyto $gold złota.")
            }
        }
        table.itemChances.forEach { (templateId, chance) ->
            if (random.nextFloat() <= chance) {
                val item = itemCatalogue.createInstance(templateId)
                if (item != null) {
                    state.inventory.add(item)
                    messages.add("Zdobyto przedmiot: ${item.name}")
                }
            }
        }
        return messages
    }
}
