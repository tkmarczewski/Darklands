package com.darklandsmobile.systems

import com.darklandsmobile.core.Enemy
import com.darklandsmobile.core.Segment

/**
 * MutacjePotworow2.0 - System mutacji potworow dla GrimReich.
 * Zarzadza Tierem Potworow (TriLayerMonster), mutacjami i adaptacja.
 */
data class MonsterMutation(
    val monsterId: Int,
    val baseTier: Int,
    var activeTier: Int,
    val mutationType: MonsterMutationType,
    val mutationIntensity: Float,
    val regionId: Int,
    val segmentId: Int,
    val isMiniboss: Boolean = false
)

enum class MonsterMutationType {
    NORMAL,
    ENRAGED,
    MUTATED,
    CORRUPTED,
    GRIMREICH_BLESSING,
    HERO
}

enum class MonsterTier {
    TIER_1,    // Basic mobs
    TIER_2,    // Elites
    TIER_3,    // Mini-bosses
    TIER_4,    // Bosses
    TIER_5     // Hero/Endgame
}

object MutacjePotworow2_0 {

    private val mutations = mutableMapOf<Int, MonsterMutation>()
    private var globalMutationFactor = 1.0f
    private val tierMultipliers = mapOf(
        1 to 1.0f,
        2 to 1.5f,
        3 to 2.0f,
        4 to 3.0f,
        5 to 5.0f
    )

    fun initialize() {
        mutations.clear()
        globalMutationFactor = 1.0f
    }

    fun createMutation(
        monsterId: Int,
        regionId: Int,
        segmentId: Int,
        baseTier: Int = 1,
        isMiniboss: Boolean = false
    ): MonsterMutation {
        val mutation = MonsterMutation(
            monsterId = monsterId,
            baseTier = baseTier,
            activeTier = baseTier,
            mutationType = MonsterMutationType.NORMAL,
            mutationIntensity = 0.0f,
            regionId = regionId,
            segmentId = segmentId,
            isMiniboss = isMiniboss
        )
        mutations[monsterId] = mutation
        return mutation
    }

    fun applyGlobalFactor(factor: Float) {
        globalMutationFactor = factor.coerceIn(0.0f, 3.0f)
        mutations.forEach { (_, m) ->
            val newTier = calculateTier(m.baseTier, m.isMiniboss, factor)
            m.activeTier = newTier
            m.mutationType = determineMutationType(m, factor)
        }
    }

    private fun calculateTier(baseTier: Int, isMiniboss: Boolean, factor: Float): Int {
        val tierBoost = when {
            factor >= 2.5f -> 2
            factor >= 2.0f -> 1
            factor >= 1.5f -> if (isMiniboss) 1 else 0
            else -> 0
        }
        return (baseTier + tierBoost).coerceIn(1, 5)
    }

    private fun determineMutationType(mutation: MonsterMutation, factor: Float): MonsterMutationType {
        return when {
            mutation.isMiniboss && factor >= 2.0f -> MonsterMutationType.GRIMREICH_BLESSING
            factor >= 2.5f -> MonsterMutationType.CORRUPTED
            factor >= 2.0f -> MonsterMutationType.MUTATED
            factor >= 1.5f -> MonsterMutationType.ENRAGED
            factor >= 1.0f -> MonsterMutationType.NORMAL
            else -> MonsterMutationType.NORMAL
        }
    }

    fun getHealthMultiplier(mutation: MonsterMutation): Float {
        val tierMult = tierMultipliers[mutation.activeTier] ?: 1.0f
        val mutationMult = when (mutation.mutationType) {
            MonsterMutationType.NORMAL -> 1.0f
            MonsterMutationType.ENRAGED -> 1.3f
            MonsterMutationType.MUTATED -> 1.7f
            MonsterMutationType.CORRUPTED -> 2.5f
            MonsterMutationType.GRIMREICH_BLESSING -> 3.0f
            MonsterMutationType.HERO -> 4.0f
        }
        return tierMult * mutationMult * globalMutationFactor
    }

    fun getDamageMultiplier(mutation: MonsterMutation): Float {
        val tierMult = 1.0f + (mutation.activeTier - 1) * 0.2f
        val grimReichBoost = if (globalMutationFactor > 1.5f) globalMutationFactor * 0.5f else 0.0f
        return (tierMult + grimReichBoost).coerceAtMost(5.0f)
    }

    fun getElementalAffinity(mutation: MonsterMutation): List<String> {
        return when (mutation.mutationType) {
            MonsterMutationType.CORRUPTED -> listOf("shadow", "decay")
            MonsterMutationType.MUTATED -> listOf("poison", "disease")
            MonsterMutationType.GRIMREICH_BLESSING -> listOf("dark_fire", "void")
            MonsterMutationType.ENRAGED -> listOf("fire", "rage")
            else -> emptyList()
        }
    }

    fun getMutation(monsterId: Int): MonsterMutation? = mutations[monsterId]
    fun getAllMutations(): Collection<MonsterMutation> = mutations.values
    fun getMutationsByRegion(regionId: Int): List<MonsterMutation> =
        mutations.filter { it.value.regionId == regionId }.values.toList()
    fun getMutationsBySegment(segmentId: Int): List<MonsterMutation> =
        mutations.filter { it.value.segmentId == segmentId }.values.toList()
    fun getMutationsByTier(tier: Int): List<MonsterMutation> =
        mutations.filter { it.value.activeTier == tier }.values.toList()

    fun applySegmentMutation(segment: Segment, factor: Float) {
        segment.monsterEncounterRate = (segment.monsterEncounterRate * factor).coerceIn(0.0f, 100.0f)
        segment.monsterDifficultyMultiplier = factor
    }
}
