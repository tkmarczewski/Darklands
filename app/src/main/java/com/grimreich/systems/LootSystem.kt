package com.grimreich.systems

import com.grimreich.core.GameRepository
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
        return itemCatalogue.getRandomItem()
    }

    fun awardLoot(chance: Float): String {
        val item = rollLoot(chance) ?: return ""
        gameRepository.currentState().inventory.add(item)
        gameRepository.persistCurrentState()
        return "Zdobyto przedmiot: ${item.name}"
    }
}
