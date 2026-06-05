package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.ItemCatalogue

data class Recipe(
    val id: String,
    val resultName: String,
    val ingredients: Map<String, Int>, // itemId -> count
    val resultItem: Item
)

object AlchemySystem {
    
    val recipes = listOf(
        Recipe(
            "brew_hp", 
            "Mikstura Zdrowia", 
            mapOf("ing_herb" to 1, "ing_water" to 1),
            ItemCatalogue.findById("potion_hp") ?: Item("potion_hp", "Mikstura Zdrowia", "potion", effects = mapOf("heal" to 15))
        )
    )
    
    fun canBrew(recipe: Recipe): Boolean {
        val inventory = GameRepository.state.inventory
        return recipe.ingredients.all { (id, count) ->
            inventory.count { it.id == id } >= count
        }
    }
    
    fun brew(recipe: Recipe): String {
        if (!canBrew(recipe)) return "Brak składników!"
        
        val inventory = GameRepository.state.inventory
        recipe.ingredients.forEach { (id, count) ->
            repeat(count) {
                val item = inventory.firstOrNull { it.id == id }
                if (item != null) inventory.remove(item)
            }
        }
        
        inventory.add(recipe.resultItem)
        return "Pomyślnie uwarzono: ${recipe.resultName}."
    }
}
