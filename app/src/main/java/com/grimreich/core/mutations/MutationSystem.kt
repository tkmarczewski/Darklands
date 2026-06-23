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
        val mutationChance = if (currentStability < 50) 0.15f else 0.02f
        val evolutionChance = if (currentStability < 30) 0.10f else 0.03f
        
        if (Random.nextFloat() < mutationChance) {
            val available = MutationRegistry.allMutations.filter { m -> 
                hero.activeMutations.none { it.id == m.id }
            }
            
            if (available.isNotEmpty()) {
                val newMutation = available.random().copy(tier = MutationTier.MANIFESTED)
                applyMutation(hero, newMutation)
                gameRepository.log("${hero.name} manifestuje nową mutację: ${newMutation.name}!")
            }
        } else if (hero.activeMutations.isNotEmpty() && Random.nextFloat() < evolutionChance) {
            evolveExistingMutation(hero)
        }
    }

    private fun evolveExistingMutation(hero: Hero) {
        val evolvable = hero.activeMutations.filter { it.tier != MutationTier.TRANSCENDENT }
        if (evolvable.isNotEmpty()) {
            val target = evolvable.random()
            val nextTier = when (target.tier) {
                MutationTier.DORMANT -> MutationTier.MANIFESTED
                MutationTier.MANIFESTED -> MutationTier.DOMINANT
                MutationTier.DOMINANT -> MutationTier.TRANSCENDENT
                MutationTier.TRANSCENDENT -> MutationTier.TRANSCENDENT
            }
            
            val updated = target.copy(tier = nextTier)
            // Replace mutation in hero list
            val index = hero.activeMutations.indexOfFirst { it.id == target.id }
            if (index != -1) {
                hero.activeMutations[index] = updated
                // Re-apply modifiers (simplified: apply bonus for reaching next tier)
                applyTierBonus(hero, updated)
                gameRepository.log("Mutacja ${updated.name} u ${hero.name} ewoluowała do poziomu ${updated.tier}!")
            }
        }
    }

    private fun applyTierBonus(hero: Hero, mutation: Mutation) {
        // Higher tiers give additional random attribute points
        val bonusAttr = mutation.attributeModifiers.keys.randomOrNull() ?: "strength"
        val bonusValue = when (mutation.tier) {
            MutationTier.DOMINANT -> 2
            MutationTier.TRANSCENDENT -> 4
            else -> 1
        }
        
        modifyHeroStat(hero, bonusAttr, bonusValue)
    }

    private fun applyMutation(hero: Hero, mutation: Mutation) {
        hero.activeMutations.add(mutation)
        
        // Apply immediate stat changes
        mutation.attributeModifiers.forEach { (attr, mod) ->
            modifyHeroStat(hero, attr, mod)
        }
        
        gameRepository.updateState { state ->
            state.world.globalStability += mutation.stabilityImpact
        }
    }

    private fun modifyHeroStat(hero: Hero, attr: String, value: Int) {
        when (attr.lowercase()) {
            "strength" -> hero.strength += value
            "agility" -> hero.agility += value
            "perception" -> hero.perception += value
            "intelligence" -> hero.intelligence += value
            "endurance" -> hero.endurance += value
            "charisma" -> hero.charisma += value
            "piety" -> hero.piety += value
        }
    }
}
