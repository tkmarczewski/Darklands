package com.grimreich.systems

import javax.inject.Inject
import javax.inject.Singleton

enum class BossLayer { REGIONAL, WORLD, HERO }

data class BossPhase(
    val phaseNumber: Int,
    val healthThreshold: Float,
    val description: String = ""
)

data class TriLayerBoss(
    val bossId: Int,
    val name: String,
    val layer: BossLayer,
    var baseHealth: Float,
    var isDefeated: Boolean = false
)

@Singleton
class TriLayerBoss2_0 @Inject constructor() {
    private val bosses = mutableMapOf<Int, TriLayerBoss>()

    fun initialize() {
        bosses.clear()
    }

    fun getBoss(id: Int): TriLayerBoss? = bosses[id]
}
