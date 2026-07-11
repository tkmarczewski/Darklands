package com.grimreich.grimreich.v1

import kotlin.random.Random

class LootMapper(private val itemsById: Map<String, Item>) {
    fun mapEntryToItem(entry: LootEntry): Item? = itemsById[entry.itemId]
    fun rollReward(table: RewardTable, rng: Random = Random.Default): List<Pair<Item, Int>> {
        val entries = table.entries
        val totalWeight = entries.sumOf { it.weight }
        if (entries.isEmpty() || totalWeight <= 0) return emptyList()
        val pick = rng.nextInt(totalWeight)
        var acc = 0
        for (entry in entries) {
            acc += entry.weight
            if (pick < acc) {
                val qty = if (entry.maxQty > entry.minQty) rng.nextInt(entry.minQty, entry.maxQty + 1) else entry.minQty
                val item = mapEntryToItem(entry)
                return if (item != null) listOf(item to qty) else emptyList()
            }
        }
        return emptyList()
    }
    fun rollMany(table: RewardTable, trials: Int, rng: Random = Random.Default): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        repeat(trials) {
            for ((item, qty) in rollReward(table, rng)) result[item.templateId] = result.getOrDefault(item.templateId, 0) + qty
        }
        return result
    }
}
