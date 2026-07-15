package com.grimreich.systems

import com.grimreich.core.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RitualSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val combatSystem: CombatSystem,
) {
    /**
     * Główna metoda wykonania rytuału.
     */
    fun performRitual(recipe: RitualRecipe, playerCipher: List<SymbolType>): Boolean {
        var success = false
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == state.activeHeroId } ?: return@updateState
            
            // 1. Sprawdź składniki
            val hasIngredients = recipe.requiredIngredients.all { ingredientId ->
                state.inventory.any { it.templateId == ingredientId }
            }

            if (!hasIngredients) {
                state.logEntries.add("RYTUAŁ: Brak odpowiednich składników.")
                return@updateState
            }

            // 2. Koszt Krwi (HP Sacrifice)
            hero.hp -= recipe.sacrificeHp
            state.logEntries.add("RYTUAŁ: Krew została przelana. (${recipe.sacrificeHp} HP)")
            
            if (hero.hp <= 0) {
                hero.hp = 1 // Minimalne życie, aby nie umrzeć w trakcie (chyba że tak chcemy)
                state.logEntries.add("RYTUAŁ: Jesteś na krawędzi istnienia.")
            }

            if (recipe.requiredCipher == playerCipher) {
                // Sukces: Usuń składniki i dodaj przedmiot
                recipe.requiredIngredients.forEach { id ->
                    state.inventory.find { it.templateId == id }?.let { item ->
                        state.inventory.remove(item)
                    }
                }
                
                val newItem = gameRepository.itemCatalogue.createInstance(recipe.targetItemId)
                if (newItem != null) {
                    state.inventory.add(newItem)
                    state.logEntries.add("RYTUAŁ: ${recipe.successMessage}")
                    success = true
                }
            } else {
                // Porażka: Pęknięcie rzeczywistości (Ambusz)
                state.logEntries.add("RYTUAŁ: Szyfr jest błędny! Rzeczywistość pęka.")
                val enemy = Bestiary.get(EnemyType.BLOOD_WRAITH)
                combatSystem.startCombat(enemy)
                success = false
            }
            
            hero.normalize()
        }
        return success
    }
}
