package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.ItemCatalogue
import kotlin.random.Random

object LootSystem {
    
    fun rollLoot(chance: Float = 0.2f): Item? {
        if (Random.nextFloat() > chance) return null
        return ItemCatalogue.all().randomOrNull()
    }
    
    fun awardLoot(chance: Float = 0.2f): String {
        val item = rollLoot(chance) ?: return ""
        GameRepository.state.inventory.add(item)
        return "\nZnaleziono przedmiot: ${item.name}!"
    }
}
