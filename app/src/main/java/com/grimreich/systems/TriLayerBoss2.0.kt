package com.grimreich.systems

import com.grimreich.core.Enemy
import com.grimreich.core.Segment
import com.grimreich.core.WorldAI

/**
 * TriLayerBoss2.0 - System trzech warstw dla bossow w Grimreich.
 * Warstwa 1: Regional Boss (Tier 4)
 * Warstwa 2: World Boss (Tier 5)
 * Warstwa 3: Hero Boss (Tier 5+) - Reinkarnacja
 */
data class TriLayerBoss(
    val bossId: Int,
    val name: String,
    val layer: BossLayer,
    val baseHealth: Float,
    val baseDamage: Float,
    val phaseCount: Int,
    val phases: List<BossPhase>,
    val mutation: MonsterMutation? = null,
    val regionId: Int,
    val isReinkarnation: Boolean = false,
    val reinkarnationTier: Int = 0,
    val isDefeated: Boolean = false
)

enum class BossLayer {
    REGIONAL,   // Tier 4 - Boss regionu
    WORLD,      // Tier 5 - Boss swiata
    HERO        // Tier 5+ - Reinkarnowany boss
}

data class BossPhase(
    val phaseNumber: Int,
    val healthThreshold: Float,
    val newAbilities: List<String> = emptyList(),
    val damageMultiplier: Float = 1.0f,
    val speedMultiplier: Float = 1.0f,
    val description: String = ""
)

object TriLayerBoss2_0 {

    private val bosses = mutableMapOf<Int, TriLayerBoss>()
    private var reinkarnationActive = false
    private var currentReinkarnationTier = 0

    fun initialize() {
        bosses.clear()
        reinkarnationActive = false
        currentReinkarnationTier = 0
    }

    fun addBoss(boss: TriLayerBoss) {
        bosses[boss.bossId] = boss
    }

    fun createRegionalBoss(
        id: Int,
        name: String,
        health: Float = 2000.0f,
        damage: Float = 150.0f,
        regionId: Int
    ): TriLayerBoss {
        val boss = TriLayerBoss(
            bossId = id,
            name = name,
            layer = BossLayer.REGIONAL,
            baseHealth = health,
            baseDamage = damage,
            phaseCount = 2,
            phases = listOf(
                BossPhase(1, 0.6f, listOf("basic_attack")),
                BossPhase(2, 0.3f, listOf("enraged_attack", "summon_minions")),
                BossPhase(3, 0.0f, listOf("ultimate"), damageMultiplier = 1.5f)
            ),
            regionId = regionId
        )
        bosses[id] = boss
        return boss
    }

    fun createWorldBoss(
        id: Int,
        name: String,
        health: Float = 10000.0f,
        damage: Float = 300.0f
    ): TriLayerBoss {
        val boss = TriLayerBoss(
            bossId = id,
            name = name,
            layer = BossLayer.WORLD,
            baseHealth = health,
            baseDamage = damage,
            phaseCount = 3,
            phases = listOf(
                BossPhase(1, 0.7f, listOf("area_attack", "summon_allies")),
                BossPhase(2, 0.4f, listOf("elemental_shift"), damageMultiplier = 1.3f, speedMultiplier = 1.2f),
                BossPhase(3, 0.1f, listOf("cataclysm", "void_blast"), damageMultiplier = 1.8f, speedMultiplier = 1.5f)
            ),
            regionId = WorldAI.CENTRAL_WORLD_ID
        )
        bosses[id] = boss
        return boss
    }

    fun createHeroBoss(
        id: Int,
        name: String,
        baseBossId: Int,
        reinkarnationTier: Int = 1
    ): TriLayerBoss? {
        val originalBoss = bosses[baseBossId] ?: return null
        val reink = TriLayerBoss(
            bossId = id,
            name = "${name} (Reinkarnacja Tier ${reinkarnationTier})",
            layer = BossLayer.HERO,
            baseHealth = originalBoss.baseHealth * (1.5f + (reinkarnationTier - 1) * 0.3f),
            baseDamage = originalBoss.baseDamage * (1.5f + (reinkarnationTier - 1) * 0.2f),
            phaseCount = originalBoss.phaseCount + 1,
            phases = originalBoss.phases + listOf(
                BossPhase(
                    originalBoss.phaseCount + 1,
                    0.0f,
                    listOf("ultimate_reinkarnation", "grimreich_power"),
                    damageMultiplier = 2.0f + reinkarnationTier * 0.2f,
                    speedMultiplier = 1.5f + reinkarnationTier * 0.1f,
                    description = "Faza reinkarnacji Grimreich"
                )
            ),
            regionId = originalBoss.regionId,
            isReinkarnation = true,
            reinkarnationTier = reinkarnationTier
        )
        bosses[id] = reink
        return reink
    }

    fun triggerReinkarnation(baseBossId: Int, tier: Int): TriLayerBoss? {
        return createHeroBoss(
            id = 10000 + baseBossId * 100 + tier,
            name = bosses[baseBossId]?.name ?: "Unknown Boss",
            baseBossId = baseBossId,
            reinkarnationTier = tier
        )
    }

    fun markBossDefeated(bossId: Int) {
        bosses[bossId]?.let { boss ->
            bosses[bossId] = boss.copy(isDefeated = true)
        }
    }

    fun getBossesByLayer(layer: BossLayer): List<TriLayerBoss> =
        bosses.filter { it.value.layer == layer }.values.toList()

    fun getRegionalBossForRegion(regionId: Int): TriLayerBoss? =
        bosses.values.firstOrNull { it.layer == BossLayer.REGIONAL && it.regionId == regionId }

    fun getActiveBosses(): List<TriLayerBoss> = bosses.values.filter { !it.isDefeated }
    fun getBoss(bossId: Int): TriLayerBoss? = bosses[bossId]
    fun getAllBosses(): Collection<TriLayerBoss> = bosses.values

    fun isBossReinkarnated(bossId: Int): Boolean = bosses[bossId]?.isReinkarnation ?: false
    fun getReinkarnationTier(bossId: Int): Int = bosses[bossId]?.reinkarnationTier ?: 0

    fun getPhasesForBoss(bossId: Int): List<BossPhase>? = bosses[bossId]?.phases

    fun getCurrentPhase(bossId: Int, currentHealth: Float): BossPhase? {
        val boss = bosses[bossId] ?: return null
        val healthPercent = currentHealth / boss.baseHealth
        return boss.phases.lastOrNull { phase ->
            phase.healthThreshold >= healthPercent
        }
    }

    fun isBossInPhase(bossId: Int, currentHealth: Float, phaseNumber: Int): Boolean {
        val phase = getCurrentPhase(bossId, currentHealth) ?: return false
        return phase.phaseNumber == phaseNumber
    }
}
