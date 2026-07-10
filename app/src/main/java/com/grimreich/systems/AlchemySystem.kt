package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.world.ItemCatalogue
import javax.inject.Inject
import javax.inject.Singleton

data class Recipe(
    val id: String,
    val resultItemId: String,
    val ingredients: Map<String, Int>,
    val minIntelligence: Int
)

@Singleton
class AlchemySystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val itemCatalogue: ItemCatalogue
) {
    val recipes = listOf(
        Recipe("rec_healing", "pot_heal", mapOf("ing_herb" to 2), 10),
        Recipe("rec_sanity", "pot_sanity", mapOf("ing_herb" to 1, "ing_blue_dust" to 1), 14),
        Recipe("rec_strength", "pot_str", mapOf("ing_bone" to 2, "ing_red_dust" to 1), 12),
        Recipe("rec_agility", "pot_agi", mapOf("ing_feather" to 2, "ing_yellow_dust" to 1), 12),
        Recipe("rec_mana", "pot_mana", mapOf("ing_blue_dust" to 2), 15)
    )

    fun craft(recipe: Recipe, heroId: String): String {
        val resultItem = itemCatalogue.get(recipe.resultItemId) ?: return "Błąd: Nie znaleziono receptury."
        var result = ""
        
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId }
                ?: run { result = "Brak bohatera."; return@updateState }

            if (hero.intelligence < recipe.minIntelligence) {
                result = "${hero.name} nie rozumie tej formuły (wymagane INT ${recipe.minIntelligence})."
                return@updateState
            }
            
            for ((ingId, qty) in recipe.ingredients) {
                val count = state.inventory.count { it.templateId == ingId }
                if (count < qty) {
                    result = "Brak składnika: $ingId ($count/$qty)."
                    return@updateState
                }
            }

            recipe.ingredients.forEach { (ingId, qty) ->
                repeat(qty) {
                    state.inventory.find { it.templateId == ingId }?.let { state.inventory.remove(it) }
                }
            }
            itemCatalogue.createInstance(recipe.resultItemId)?.let { state.inventory.add(it) }
            result = "Sukces! Uwarzono ${resultItem.name}."
            state.logEntries.add("Alchemia: $result")
        }
        
        return result
    }
}
