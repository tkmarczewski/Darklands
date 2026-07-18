package com.grimreich.core.mutations

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.Hero
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

// FIX: MutationRandomProvider interface enables deterministic testing via constructor injection.
// Production code uses DefaultMutationRng (delegates to kotlin.random.Random).
// Tests inject AlwaysTriggerMutationRng / NeverTriggerMutationRng for fully deterministic
// behaviour — no flaky loops needed (required by MutationSystemTest).
interface MutationRandomProvider {
    fun shouldTrigger(probability: Float): Boolean
    fun nextFloat(): Float
}

class DefaultMutationRng(private val seed: Long? = null) : MutationRandomProvider {
    private val rng: Random get() = if (seed != null) Random(seed) else Random.Default
    override fun shouldTrigger(probability: Float): Boolean = rng.nextFloat() < probability
    override fun nextFloat(): Float = rng.nextFloat()
}

@Singleton
class MutationSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val rngProvider: MutationRandomProvider = DefaultMutationRng()
) {

    companion object {
        private const val MAX_MUTATIONS = 10
        private const val STAT_CAP = 99
    }

    // FIX: reverted from suspend back to a plain function. The only production
    // call-site (MutationEngine.processMutations) calls this synchronously
    // inside gameRepository.updateState { }, which is itself synchronous again.
    fun checkForNewMutation(heroId: String, regionId: String, currentStability: Int) {
        gameRepository.updateState { state ->
            checkForNewMutationDirect(state, heroId, regionId, currentStability)
        }
    }

    /**
     * Direct version for use inside updateState blocks to avoid multiple state clones.
     * Part of the Final Technical Polish.
     */
    fun checkForNewMutationDirect(state: GameState, heroId: String, regionId: String, currentStability: Int) {
        val mutationChance = if (currentStability < 50) 0.15f else 0.02f
        val evolutionChance = if (currentStability < 30) 0.10f else 0.03f

        val hero = state.party.find { it.id == heroId } ?: return

        if (rngProvider.shouldTrigger(mutationChance)) {
            val available = MutationRegistry.allMutations.filter { m ->
                hero.activeMutations.none { it.id == m.id }
            }

            if (available.isNotEmpty() && hero.activeMutations.size < MAX_MUTATIONS) {
                // FIX: seeded RNG for deterministic selection within a given game day/hero/region.
                val selectionRng = Random(
                    heroId.hashCode().toLong() + regionId.hashCode().toLong() + state.world.day.toLong()
                )
                val newMutation = available.random(selectionRng).copy(tier = MutationTier.MANIFESTED)
                hero.activeMutations.add(newMutation)
                newMutation.attributeModifiers.forEach { (attr, mod) ->
                    modifyHeroStat(hero, attr, mod)
                }
                state.world.globalStability = (state.world.globalStability + newMutation.stabilityImpact)
                    .coerceIn(0, 100)
                state.logEntries.add("${hero.name} manifestuje nową mutację: ${newMutation.name}!")
            }
        } else if (hero.activeMutations.isNotEmpty() && rngProvider.shouldTrigger(evolutionChance)) {
            val evolvable = hero.activeMutations.filter { it.tier != MutationTier.TRANSCENDENT }
            if (evolvable.isNotEmpty()) {
                val selectionRng = Random(
                    heroId.hashCode().toLong() + regionId.hashCode().toLong() + state.world.day.toLong() + 1L
                )
                val target = evolvable.random(selectionRng)
                val nextTier = when (target.tier) {
                    MutationTier.dormant      -> MutationTier.manifested
                    MutationTier.manifested   -> MutationTier.dominant
                    MutationTier.dominant     -> MutationTier.transcendent
                    MutationTier.transcendent -> MutationTier.transcendent
                    else                      -> MutationTier.manifested
                }

                val updated = target.copy(tier = nextTier)
                val index = hero.activeMutations.indexOfFirst { it.id == target.id }
                if (index != -1) {
                    hero.activeMutations[index] = updated
                    applyTierBonus(hero, updated)
                    state.logEntries.add("Mutacja ${updated.name} u ${hero.name} ewoluowała do poziomu ${updated.tier}!")
                }
            }
        }
    }

    private fun applyTierBonus(hero: Hero, mutation: Mutation) {
        val selectionRng = Random(mutation.id.hashCode().toLong())
        val bonusAttr = mutation.attributeModifiers.keys.randomOrNull(selectionRng) ?: "strength"
        val bonusValue = when (mutation.tier) {
            MutationTier.dominant     -> 2
            MutationTier.transcendent -> 3
            else                      -> 1
        }
        modifyHeroStat(hero, bonusAttr, bonusValue)
    }

    private fun modifyHeroStat(hero: Hero, attr: String, value: Int) {
        when (attr.lowercase()) {
            "strength"     -> hero.strength     = (hero.strength + value).coerceIn(0, STAT_CAP)
            "agility"      -> hero.agility      = (hero.agility + value).coerceIn(0, STAT_CAP)
            "perception"   -> hero.perception   = (hero.perception + value).coerceIn(0, STAT_CAP)
            "intelligence" -> hero.intelligence = (hero.intelligence + value).coerceIn(0, STAT_CAP)
            "endurance"    -> hero.endurance    = (hero.endurance + value).coerceIn(0, STAT_CAP)
            "charisma"     -> hero.charisma     = (hero.charisma + value).coerceIn(0, STAT_CAP)
            "piety"        -> hero.piety        = (hero.piety + value).coerceIn(0, STAT_CAP)
        }
    }
}
