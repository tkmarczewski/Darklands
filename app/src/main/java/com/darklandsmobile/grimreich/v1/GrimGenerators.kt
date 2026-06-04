package com.darklandsmobile.grimreich.v1

import kotlin.random.Random

data class GrimQuest(val id: String, val title: String, val description: String, val regionName: String, val relatedNpc: String?, val phenomenon: String, val difficulty: Int, val rewards: List<String>)
data class GrimWorldEvent(val id: String, val regionName: String, val title: String, val description: String, val impactOnReputation: Int, val impactOnCollapse: Int)
data class GeneratedLoot(val entries: List<LootEntry>)

class NpcGenerator(private val random: Random = Random.Default) {
    fun generateNpc(regionName: String, phenomenon: String, power: Int): NPCLifePath {
        val name = listOf("Aelion", "Xyrel", "Sereth", "Mira", "Rhovan", "Helga", "Orlan", "Nysa").random(random) + "-${random.nextInt(100,999)}"
        return NPCLifePath(name, "związany z $phenomenon w $regionName", "mutacja ciała pod wpływem $phenomenon", "dusza rozbita przez $regionName", power.coerceAtMost(10), (power + 1).coerceAtMost(10), listOf("narodziny", "pęknięcie", "Druga Strona"), "wpływ_${regionName.lowercase()}", "końcowy_wpływ_${phenomenon.lowercase()}")
    }
    fun generateRelationships(npcName: String): List<TriLayerRelationship> = listOf(TriLayerRelationship(npcName, "ostrożność", "dyscyplina", "kontrola", 3, 2, "napięcie", "region_impuls", "ending_impuls"))
}

class QuestGenerator(private val random: Random = Random.Default) {
    fun generateQuest(regionName: String, phenomenon: String, npc: NPCLifePath?, difficulty: Int): GrimQuest {
        val id = "q_${regionName.lowercase()}_${random.nextInt(1000, 9999)}"
        val title = "Zadanie: $regionName"
        val description = "Zwiąż $phenomenon z losem regionu $regionName. ${npc?.npcName ?: "Nieznany"} zna cenę."
        val rewards = when { difficulty <= 3 -> listOf("mist_shard"); difficulty <= 6 -> listOf("mist_shard", "blood_seal"); else -> listOf("mist_shard", "blood_seal", "mirror_token") }
        return GrimQuest(id, title, description, regionName, npc?.npcName, phenomenon, difficulty, rewards)
    }
}

class WorldEventGenerator(private val random: Random = Random.Default) {
    fun generateEvent(region: RegionConsciousness, time: NonlinearTime, collapse: WorldCollapse): GrimWorldEvent {
        val id = "e_${region.regionName.lowercase()}_${random.nextInt(1000,9999)}"
        val title = if (collapse.phenomenonLoss >= 7) "Załamanie fenomenu w ${region.regionName}" else "Zdarzenie ${region.regionName}"
        val description = "Emocja regionu: ${region.emotionalState}. Pamięć: ${region.memory.joinToString(", ")}."
        return GrimWorldEvent(id, region.regionName, title, description, -1, (collapse.phenomenonLoss + collapse.layerCollapse).coerceAtLeast(0))
    }
}

class ExpeditionGenerator(private val random: Random = Random.Default) {
    fun generateExpedition(regionName: String, phenomenon: String, chaosLevel: Int, difficulty: Int): OtherSideExpedition {
        val enemies = when (phenomenon.lowercase()) { "mist" -> listOf("fog_wraith", "silent_monk"); "blood" -> listOf("blood_husk", "scar_beast"); "reflection" -> listOf("mirror_knight", "echo_clone"); else -> listOf("fractureling", "wanderer") }
        val rewards = when { chaosLevel >= 7 -> listOf("mist_shard", "mirror_token", "blood_seal"); chaosLevel >= 4 -> listOf("mist_shard", "blood_seal"); else -> listOf("mist_shard") }
        return OtherSideExpedition("Druga Strona: $regionName", "Logic-$regionName", "Sym-$regionName", "Zero-$regionName", enemies, rewards, "oscillation_$regionName", "branch_${phenomenon.lowercase()}", difficulty.coerceAtLeast(1))
    }
}

class LootRoller(private val random: Random = Random.Default) {
    fun roll(baseRewards: List<String>, summary: OtherSideReward, rolls: Int): GeneratedLoot = GeneratedLoot(List(rolls.coerceAtLeast(1)) { LootEntry("${baseRewards.ifEmpty { listOf("scrap") }.random(random)}-${random.nextInt(1000,9999)}") })
}
