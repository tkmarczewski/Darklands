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

    companion object {
        // BUG-R3-02: Hard cap on active mutations per hero to prevent unbounded stack growth
        private const val MAX_MUTATIONS = 10
        // BUG-R3-01: Maximum value any single hero stat can reach via mutation
        private const val STAT_CAP = 99
    }

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
        // BUG-R3-04: Cap tier bonus values to prevent runaway stat inflation
        val bonusAttr = mutation.attributeModifiers.keys.randomOrNull() ?: "strength"
        val bonusValue = when (mutation.tier) {
            MutationTier.DOMINANT -> 2
            MutationTier.TRANSCENDENT -> 3  // Reduced from 4 to limit inflation
            else -> 1
        }

        modifyHeroStat(hero, bonusAttr, bonusValue)
    }

    private fun applyMutation(hero: Hero, mutation: Mutation) {
        // BUG-R3-02: Enforce mutation stack cap to prevent unbounded growth
        if (hero.activeMutations.size >= MAX_MUTATIONS) {
            gameRepository.log("${hero.name} osiągnął limit mutacji ($MAX_MUTATIONS). Nowa mutacja odrzucona.")
            return
        }

        hero.activeMutations.add(mutation)

        // Apply immediate stat changes
        mutation.attributeModifiers.forEach { (attr, mod) ->
            modifyHeroStat(hero, attr, mod)
        }

        // BUG-R3-03: Clamp globalStability to valid range [0, 100] after mutation impact
        gameRepository.updateState { state ->
            state.world.globalStability = (state.world.globalStability + mutation.stabilityImpact)
                .coerceIn(0, 100)
        }
    }

    private fun modifyHeroStat(hero: Hero, attr: String, value: Int) {
        // BUG-R3-01: Clamp all stats to [0, STAT_CAP] to prevent unbounded growth
        when (attr.lowercase()) {
            "strength" -> hero.strength = (hero.strength + value).coerceIn(0, STAT_CAP)
            "agility" -> hero.agility = (hero.agility + value).coerceIn(0, STAT_CAP)
            "perception" -> hero.perception = (hero.perception + value).coerceIn(0, STAT_CAP)
            "intelligence" -> hero.intelligence = (hero.intelligence + value).coerceIn(0, STAT_CAP)
            "endurance" -> hero.endurance = (hero.endurance + value).coerceIn(0, STAT_CAP)
            "charisma" -> hero.charisma = (hero.charisma + value).coerceIn(0, STAT_CAP)
            "piety" -> hero.piety = (hero.piety + value).coerceIn(0, STAT_CAP)
        }
    }
}
