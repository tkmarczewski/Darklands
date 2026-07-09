package com.grimreich.grimreich.v1

import com.grimreich.core.GameState

data class GrimWorld(val id: String, val name: String, val regions: List<Region>, val factions: List<Faction>, val notes: String?)
data class Region(val id: String, val name: String, val description: String, val encounters: List<Encounter>, val seed: Long?)

enum class OntologicalLevel(val level: Int, val displayName: String) {
    MATERIAL(1, "Materialne"),
    ALTERED(2, "Zmienione"),
    SPIRITS(3, "Duchy"),
    RELICS(4, "Relikwie"),
    UNIQUE(5, "Unikalne"),
    COLOSSI(6, "Kolosy"),
    APOCALYPSE(7, "Apokalipsa"),
    TRANSCENDENCE(8, "Transcendencja"),
    IDEAS(9, "Idee"),
    IMPOSSIBILITY(10, "Niemożliwość"),
    COSMOS(11, "Kosmos"),
    ABSOLUTE(12, "Absolutne"),
    META_NARRATION(13, "Meta-Narracja"),
    ABSOLUTE_SCRIBES(14, "Skrybowie Absolutni")
}

enum class ReputationLevel(val minScore: Int, val displayName: String) {
    HATED(-100, "Znienawidzony"),
    HOSTILE(-50, "Wrogi"),
    NEUTRAL(0, "Neutralny"),
    FRIENDLY(50, "Przyjazny"),
    EXALTED(100, "Wywyższony");

    companion object {
        fun fromScore(score: Int): ReputationLevel {
            return entries.sortedByDescending { it.minScore }.firstOrNull { score >= it.minScore } ?: HATED
        }
    }
}

data class NPC(
    val id: String,
    val name: String,
    val role: String,
    val factionId: String? = null,
    val personality: String = "Normal",
    val stats: Map<String, Int> = emptyMap(),
    val inventory: List<Item> = emptyList(),
    val dialogue: Map<String, Any>? = null,
    val startNodeId: String? = null,
    val stability: Float = 1.0f,
    val isInfested: Boolean = false,
    val isRegionalHero: Boolean = false,
    val interactionHistory: MutableMap<String, Int> = mutableMapOf()
)

data class ChronicleEntry(
    val id: String,
    val title: String,
    val fullText: String,
    val category: String, // e.g. "Era of Fracture", "Saints", "Factions"
    val unlocked: Boolean = false
)

data class Boss(val id: String, val name: String, val level: Int, val lootTable: RewardTable)

data class Item(
    val id: String,
    val name: String,
    val type: String,
    val slot: String? = null,
    val value: Int = 0,
    val weight: Double = 0.0,
    val rarity: String = "common",
    val lore: String = "",
    val properties: Map<String, Any> = emptyMap(),
    val effects: Map<String, Int> = emptyMap()
)

data class LootEntry(val itemId: String, val weight: Int = 1, val minQty: Int = 1, val maxQty: Int = 1)
data class GeneratedLoot(val entries: List<LootEntry>)
data class RewardTable(val id: String, val entries: List<LootEntry>)
data class Encounter(val id: String, val name: String, val difficulty: Int, val possibleNpcs: List<String>)
data class Faction(val id: String, val name: String, val disposition: String)
data class Quest(val id: String, val title: String, val description: String, val rewards: RewardTable)
data class Skill(val id: String, val name: String, val power: Int)
data class Equipment(val id: String, val name: String, val slot: String, val stats: Map<String, Int>)

data class DialogueChoice(
    val text: String,
    val targetNodeId: String,
    val requiredReputation: Int = 0,
    val factionId: String? = null,
    val requiredAttributes: Map<String, Int> = emptyMap(),
    val requiredSkills: Map<String, Int> = emptyMap(),
    val requiredQuestId: String? = null,
    val triggerEvent: String? = null,
    val triggerValue: String? = null,
    val isCombatTrigger: Boolean = false,
    @Transient val onSelect: ((GameState) -> Unit)? = {}
)

data class DialogueNode(
    val id: String,
    val npcId: String,
    val text: String,
    val choices: List<DialogueChoice> = emptyList()
)
