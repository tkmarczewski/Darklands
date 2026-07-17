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
            val hpCost = recipe.sacrificeHp.coerceAtLeast(0)
            hero.hp -= hpCost
            state.logEntries.add("RYTUAŁ: Krew została przelana. ($hpCost HP)")
            
            // Normalize and check death BEFORE continuing
            hero.normalize()
            
            if (hero.isDead) {
                state.logEntries.add("RYTUAŁ: Ofiara była zbyt wielka. ${hero.name} poległ.")
                return@updateState
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
                val enemy = Bestiary.get(EnemyType.blood_wraith)
                combatSystem.startCombat(enemy)
                success = false
            }
            
            hero.normalize()
        }
        return success
    }

    // --- OLD RITUAL METHODS (Refactoring Compatibility) ---

    fun canPerformResurrection(hero: Hero, gold: Int): Boolean {
        return hero.isDead && gold >= 100
    }

    fun performResurrection(heroId: String): Boolean {
        var success = false
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId }
            if (hero != null && state.gold >= 100) {
                state.gold -= 100
                hero.isDead = false
                hero.hp = 1
                hero.sanity -= 15
                hero.corruption += 20
                state.world.globalStability -= 15
                state.logEntries.add("RYTUAŁ: ${hero.name} powrócił z Pęknięcia, ale nie jest już taki sam.")
                hero.normalize()
                success = true
            }
        }
        return success
    }

    fun sacrificeHero(heroId: String) {
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId }
            if (hero != null) {
                state.party.remove(hero)
                state.logEntries.add("RYTUAŁ: Ciało ${hero.name} zostało złożone w ofierze. Pustka jest zadowolona.")
            }
        }
    }
}
