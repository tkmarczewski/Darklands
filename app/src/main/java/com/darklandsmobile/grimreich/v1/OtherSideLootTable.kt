package com.darklandsmobile.grimreich.v1

import kotlin.random.Random

enum class LootRarity { COMMON, RARE, RELIC, CURSE }
enum class LootType { MIST, BLOOD, RELIC_ITEM, CURSE_TOKEN }

data class LootEntry(
    val id: String,
    val type: LootType,
    val rarity: LootRarity,
    val baseWeight: Int
)

data class GeneratedLoot(val entries: List<LootEntry>)

class OtherSideLootTable(private val random: Random = Random.Default) {
    private val baseTable = listOf(
        LootEntry("mist_shard_common", LootType.MIST, LootRarity.COMMON, 10),
        LootEntry("mist_shard_rare", LootType.MIST, LootRarity.RARE, 3),
        LootEntry("blood_seal_common", LootType.BLOOD, LootRarity.COMMON, 8),
        LootEntry("blood_seal_relic", LootType.BLOOD, LootRarity.RELIC, 1),
        LootEntry("relic_icon", LootType.RELIC_ITEM, LootRarity.RELIC, 1),
        LootEntry("curse_token", LootType.CURSE_TOKEN, LootRarity.CURSE, 5)
    )

    fun generate(
        baseRewards: List<String>,
        rewardSummary: OtherSideReward,
        difficultyTier: Int = 1,
        regionChaosLevel: Int = 0,
        rolls: Int = 3
    ): GeneratedLoot {
        val entries = mutableListOf<LootEntry>()
        fun addById(id: String) { baseTable.firstOrNull { it.id == id }?.let(entries::add) }
        baseRewards.forEach { addById(mapBaseToEntryId(it)) }
        rewardSummary.finalRewards.forEach {
            when (it) {
                "bonus_mist_shard" -> addById("mist_shard_rare")
                "penalty_curse" -> addById("curse_token")
            }
        }
        val weightBoost = rewardSummary.finalRewards.count { it == "bonus_mist_shard" } + (difficultyTier - 1)
        val curseBoost = rewardSummary.finalRewards.count { it == "penalty_curse" } + regionChaosLevel.coerceAtMost(3)
        val weightedTable = baseTable.map { entry ->
            val bonus = when (entry.id) {
                "mist_shard_rare" -> weightBoost
                "curse_token" -> curseBoost
                else -> 0
            }
            entry.copy(baseWeight = (entry.baseWeight + bonus).coerceAtLeast(1))
        }
        repeat(rolls) { pickWeighted(weightedTable)?.let(entries::add) }
        return GeneratedLoot(entries)
    }

    private fun mapBaseToEntryId(baseId: String): String = when (baseId) {
        "mist_shard" -> "mist_shard_common"
        "blood_seal" -> "blood_seal_common"
        else -> baseId
    }

    private fun pickWeighted(table: List<LootEntry>): LootEntry? {
        val total = table.sumOf { it.baseWeight }
        if (total <= 0) return null
        var roll = random.nextInt(total)
        for (e in table) {
            roll -= e.baseWeight
            if (roll < 0) return e
        }
        return table.lastOrNull()
    }
}
