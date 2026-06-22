package com.grimreich.core.mutations

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MutationSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun checkForNewMutation(hero: Hero, regionId: String, currentStability: Int) {
        // Base logic for mutation gain
        // Stability below 50 increases chance significantly
        val chance = if (currentStability < 50) 0.15f else 0.02f
        
        if (Random.nextFloat() < chance) {
            val available = MutationRegistry.allMutations.filter { m -> 
                hero.activeMutations.none { it.id == m.id }
            }
            
            if (available.isNotEmpty()) {
                val newMutation = available.random()
                applyMutation(hero, newMutation)
                gameRepository.log("${hero.name} manifestuje nową mutację: ${newMutation.name}!")
            }
        }
    }

    private fun applyMutation(hero: Hero, mutation: Mutation) {
        hero.activeMutations.add(mutation)
        
        // Apply immediate stat changes
        mutation.attributeModifiers.forEach { (attr, mod) ->
            when (attr.lowercase()) {
                "strength" -> hero.strength += mod
                "agility" -> hero.agility += mod
                "perception" -> hero.perception += mod
                "intelligence" -> hero.intelligence += mod
                "endurance" -> hero.endurance += mod
                "charisma" -> hero.charisma += mod
                "piety" -> hero.piety += mod
            }
        }
        
        gameRepository.updateState { state ->
            state.world.globalStability += mutation.stabilityImpact
        }
    }
}
