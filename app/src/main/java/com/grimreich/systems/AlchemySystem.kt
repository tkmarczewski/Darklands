package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.ItemCatalogue
import javax.inject.Inject
import javax.inject.Singleton

data class Recipe(
    val id: String,
    val resultName: String,
    val ingredients: Map<String, Int>,
    val resultItem: Item
)

@Singleton
class AlchemySystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val itemCatalogue: ItemCatalogue
) {
    fun getRecipes() = listOf(
        Recipe("rec_heal", "Mikstura Leczenia", mapOf("herb_green" to 2), itemCatalogue.get("potion_hp")!!),
        Recipe("rec_sanity", "Eliksir Jasności", mapOf("crystal_clear" to 1), itemCatalogue.get("potion_hp")!!) // Placeholder
    )

    fun canBrew(recipe: Recipe): Boolean {
        val inventory = gameRepository.currentState().inventory
        return recipe.ingredients.all { (id, qty) ->
            inventory.count { it.id == id } >= qty
        }
    }

    fun brew(recipe: Recipe): String {
        if (!canBrew(recipe)) return "Brak składników!"

        val state = gameRepository.currentState()
        recipe.ingredients.forEach { (id, qty) ->
            repeat(qty) {
                val item = state.inventory.first { it.id == id }
                state.inventory.remove(item)
            }
        }

        state.inventory.add(recipe.resultItem)
        gameRepository.persistCurrentState()
        return "Uwarzono: ${recipe.resultName}"
    }
}
