package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.ItemCatalogue
import javax.inject.Inject
import javax.inject.Singleton

data class Recipe(
    val id: String,
    val resultItemId: String,
    val ingredients: Map<String, Int>, // ItemId -> Quantity
    val minIntelligence: Int = 10
)

@Singleton
class AlchemySystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val itemCatalogue: ItemCatalogue
) {
    val recipes = listOf(
        Recipe(
            id = "rec_potion_hp",
            resultItemId = "potion_hp",
            ingredients = mapOf("ing_echo_dust" to 2, "ing_blood_root" to 1)
        ),
        Recipe(
            id = "rec_potion_mana",
            resultItemId = "potion_mana",
            ingredients = mapOf("ing_echo_dust" to 2, "ing_mist_essence" to 1)
        ),
        Recipe(
            id = "rec_potion_sanity",
            resultItemId = "potion_sanity",
            ingredients = mapOf("ing_mist_essence" to 2, "ing_blood_root" to 1),
            minIntelligence = 14
        )
    )

    fun craft(recipe: Recipe, heroId: String): String {
        val state = gameRepository.currentState()
        val hero = state.party.find { it.id == heroId } ?: return "Brak bohatera."
        
        if (hero.intelligence < recipe.minIntelligence) {
            return "${hero.name} nie rozumie tej formuły (wymagane INT ${recipe.minIntelligence})."
        }

        // Check ingredients
        for ((ingId, qty) in recipe.ingredients) {
            val count = state.inventory.count { it.id == ingId }
            if (count < qty) {
                return "Brak składnika: ${itemCatalogue.get(ingId)?.name ?: ingId} ($count/$qty)."
            }
        }

        // Remove ingredients
        recipe.ingredients.forEach { (ingId, qty) ->
            repeat(qty) {
                val item = state.inventory.find { it.id == ingId }
                if (item != null) state.inventory.remove(item)
            }
        }

        // Add result
        val resultItem = itemCatalogue.get(recipe.resultItemId)
        if (resultItem != null) {
            state.inventory.add(resultItem.copy())
            gameRepository.log("${hero.name} uwarzył: ${resultItem.name}.")
            gameRepository.persistCurrentState()
            return "Sukces! Uwarzono ${resultItem.name}."
        }

        return "Błąd tworzenia przedmiotu."
    }
}
