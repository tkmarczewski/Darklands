package com.grimreich.systems

import javax.inject.Inject
import javax.inject.Singleton

enum class MonsterMutationType { NORMAL, ENRAGED, MUTATED, CORRUPTED, GRIMREICH_BLESSING, HERO }

data class MonsterMutation(
    val monsterId: Int,
    val baseTier: Int,
    var activeTier: Int,
    var mutationType: MonsterMutationType,
    var mutationIntensity: Float = 0f
)

@Singleton
class MutacjePotworow2_0 @Inject constructor() {
    private val mutations = mutableMapOf<Int, MonsterMutation>()

    fun initialize() {
        mutations.clear()
    }

    fun createMutation(id: Int, tier: Int): MonsterMutation {
        val m = MonsterMutation(id, tier, tier, MonsterMutationType.NORMAL)
        mutations[id] = m
        return m
    }

    fun getMutation(id: Int): MonsterMutation? = mutations[id]
}
