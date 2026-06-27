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
        private const val MAX_MUTATIONS = 10
        private const val STAT_CAP = 99
    }

    fun checkForNewMutation(heroId: String, regionId: String, currentStability: Int) {
        val state = gameRepository.currentState()
        // Determinism fix: Seed based on hero and region for "cipher" consistency
        val seed = heroId.hashCode().toLong() + regionId.hashCode().toLong() + state.world.day.toLong()
        val rng = Random(seed)

        val mutationChance = if (currentStability < 50) 0.15f else 0.02f
        val evolutionChance = if (currentStability < 30) 0.10f else 0.03f

        gameRepository.updateState { s ->
            val hero = s.party.find { it.id == heroId } ?: return@updateState

            if (rng.nextFloat() < mutationChance) {
                val available = MutationRegistry.allMutations.filter { m ->
                    hero.activeMutations.none { it.id == m.id }
                }

                if (available.isNotEmpty() && hero.activeMutations.size < MAX_MUTATIONS) {
                    val newMutation = available.random(rng).copy(tier = MutationTier.MANIFESTED)
                    hero.activeMutations.add(newMutation)
                    newMutation.attributeModifiers.forEach { (attr, mod) ->
                        modifyHeroStat(hero, attr, mod)
                    }
                    s.world.globalStability = (s.world.globalStability + newMutation.stabilityImpact)
                        .coerceIn(0, 100)
                    s.logEntries.add("${hero.name} manifestuje nową mutację: ${newMutation.name}!")
                }
            } else if (hero.activeMutations.isNotEmpty() && rng.nextFloat() < evolutionChance) {
                val evolvable = hero.activeMutations.filter { it.tier != MutationTier.TRANSCENDENT }
                if (evolvable.isNotEmpty()) {
                    val target = evolvable.random(rng)
                    val nextTier = when (target.tier) {
                        MutationTier.DORMANT -> MutationTier.MANIFESTED
                        MutationTier.MANIFESTED -> MutationTier.DOMINANT
                        MutationTier.DOMINANT -> MutationTier.TRANSCENDENT
                        MutationTier.TRANSCENDENT -> MutationTier.TRANSCENDENT
                    }

                    val updated = target.copy(tier = nextTier)
                    val index = hero.activeMutations.indexOfFirst { it.id == target.id }
                    if (index != -1) {
                        hero.activeMutations[index] = updated
                        applyTierBonus(hero, updated)
                        s.logEntries.add("Mutacja ${updated.name} u ${hero.name} ewoluowała do poziomu ${updated.tier}!")
                    }
                }
            }
        }
    }

    private fun applyTierBonus(hero: Hero, mutation: Mutation) {
        val bonusAttr = mutation.attributeModifiers.keys.randomOrNull() ?: "strength"
        val bonusValue = when (mutation.tier) {
            MutationTier.DOMINANT -> 2
            MutationTier.TRANSCENDENT -> 3
            else -> 1
        }
        modifyHeroStat(hero, bonusAttr, bonusValue)
    }

    private fun modifyHeroStat(hero: Hero, attr: String, value: Int) {
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
