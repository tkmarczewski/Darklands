package com.grimreich.grimreich.v1

import com.grimreich.core.GameState

data class GrimWorld(val id: String, val name: String, val regions: List<Region> = emptyList(), val factions: List<Faction> = emptyList(), val notes: String? = null)
data class Region(val id: String, val name: String, val description: String = "", val encounters: List<Encounter> = emptyList(), val seed: Long? = null)
data class NPC(
    val id: String, 
    val name: String, 
    val role: String, 
    val factionId: String? = null, 
    val stats: Map<String, Int> = emptyMap(), 
    val inventory: List<Item> = emptyList(), 
    val dialogue: Map<String, Any>? = null,
    val startNodeId: String? = null,
    var stability: Float = 1.0f // 1.5 Era: Stability of reality for this NPC
)
data class Boss(val id: String, val name: String, val level: Int, val lootTable: RewardTable)

// Consolidated Item class
data class Item(
    val id: String,
    val name: String,
    val type: String,
    val slot: String? = null,
    val value: Int = 0,
    val weight: Double = 0.0,
    val rarity: String = "common",
    val properties: Map<String, Any> = emptyMap(),
    val effects: Map<String, Int> = emptyMap()
)

data class LootEntry(val itemId: String, val weight: Int = 0, val minQty: Int = 1, val maxQty: Int = 1)
data class GeneratedLoot(val entries: List<LootEntry>)
data class RewardTable(val id: String, val entries: List<LootEntry> = emptyList())
data class Encounter(val id: String, val name: String, val difficulty: Int = 1, val possibleNpcs: List<String> = emptyList())
data class Faction(val id: String, val name: String, val disposition: String = "neutral")
data class Quest(val id: String, val title: String, val description: String, val rewards: RewardTable)
data class Skill(val id: String, val name: String, val power: Int)
data class Equipment(val id: String, val name: String, val slot: String, val stats: Map<String, Int> = emptyMap())

// Dialogue System
data class DialogueChoice(
    val text: String,
    val targetNodeId: String,
    val requiredReputation: Int = 0,
    val factionId: String? = null,
    val onSelect: (GameState) -> Unit = {}
)

data class DialogueNode(
    val id: String,
    val npcId: String,
    val text: String,
    val choices: List<DialogueChoice> = emptyList()
)

// Fenomeny 1.5
data class Phenomenon15(
    val id: String,
    val type: PhenomenonType,
    var corePulse: Float = 0.5f,
    var stabilityLevel: Float = 1.0f,
    var awakeningStage: Int = 0
)

enum class PhenomenonType {
    MIST, BLOOD, REFLECTION, FULLNESS, CHAOS, ZERO, ABSOLUTE
}
