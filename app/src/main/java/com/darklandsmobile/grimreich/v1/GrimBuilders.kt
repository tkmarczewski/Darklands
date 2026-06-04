package com.darklandsmobile.grimreich.v1

import java.util.UUID

data class GrimWorld(val id: String, val name: String, val regions: List<Region> = emptyList(), val factions: List<Faction> = emptyList(), val notes: String? = null)
data class Region(val id: String, val name: String, val description: String = "", val encounters: List<Encounter> = emptyList(), val seed: Long? = null)
data class NPC(val id: String, val name: String, val role: String, val factionId: String? = null, val stats: Map<String, Int> = emptyMap(), val inventory: List<Item> = emptyList(), val dialogue: Map<String, Any>? = null)
data class Boss(val id: String, val name: String, val level: Int, val lootTable: RewardTable)
data class Item(val id: String, val name: String, val type: String, val rarity: String, val properties: Map<String, Any> = emptyMap())
data class LootEntry(val itemId: String, val weight: Int = 0, val minQty: Int = 1, val maxQty: Int = 1)
data class RewardTable(val id: String, val entries: List<LootEntry> = emptyList())
data class Encounter(val id: String, val name: String, val difficulty: Int = 1, val possibleNpcs: List<String> = emptyList())
data class Faction(val id: String, val name: String, val disposition: String = "neutral")
data class Quest(val id: String, val title: String, val description: String, val rewards: RewardTable)
data class Skill(val id: String, val name: String, val power: Int)
data class Equipment(val id: String, val name: String, val slot: String, val stats: Map<String, Int> = emptyMap())

object GrimBuilders {
    fun randomId(prefix: String): String = "$prefix-${UUID.randomUUID()}"
    fun grimWorld(id: String = randomId("world"), name: String = "Grimreich", regions: List<Region> = listOf(region()), factions: List<Faction> = listOf(faction()), notes: String? = null) = GrimWorld(id, name, regions.toList(), factions.toList(), notes)
    fun region(id: String = randomId("region"), name: String = "Misty Vale", description: String = "A foggy, half-ruined city where shadows move.", encounters: List<Encounter> = listOf(encounter()), seed: Long? = null) = Region(id, name, description, encounters.toList(), seed)
    fun npc(id: String = randomId("npc"), name: String = "Unnamed", role: String = "villager", factionId: String? = null, stats: Map<String, Int> = defaultStats(), inventory: List<Item> = emptyList(), dialogue: Map<String, Any>? = null) = NPC(id, name, role, factionId, stats.toMap(), inventory.toList(), dialogue)
    fun boss(id: String = randomId("boss"), name: String = "Ancient Horror", level: Int = 10, lootTable: RewardTable = rewardTable()) = Boss(id, name, level, lootTable)
    fun item(id: String = randomId("item"), name: String = "Rusty Blade", type: String = "weapon", rarity: String = "common", properties: Map<String, Any> = emptyMap()) = Item(id, name, type, rarity, properties.toMap())
    fun lootEntry(itemId: String, weight: Int = 10, minQty: Int = 1, maxQty: Int = 1) = LootEntry(itemId, weight, minQty, maxQty)
    fun rewardTable(id: String = randomId("reward"), entries: List<LootEntry> = listOf(lootEntry(item().id, 100))) = RewardTable(id, entries.toList())
    fun encounter(id: String = randomId("enc"), name: String = "Wandering Bandits", difficulty: Int = 1, possibleNpcs: List<String> = emptyList()) = Encounter(id, name, difficulty, possibleNpcs.toList())
    fun faction(id: String = randomId("faction"), name: String = "Order of the Candle", disposition: String = "neutral") = Faction(id, name, disposition)
    fun quest(id: String = randomId("quest"), title: String = "A Small Favor", description: String = "Help the tavern keeper.", rewards: RewardTable = rewardTable()) = Quest(id, title, description, rewards)
    fun skill(id: String = randomId("skill"), name: String = "Strike", power: Int = 5) = Skill(id, name, power)
    fun equipment(id: String = randomId("equip"), name: String = "Leather Cap", slot: String = "head", stats: Map<String, Int> = mapOf("def" to 1)) = Equipment(id, name, slot, stats.toMap())
    private fun defaultStats(): Map<String, Int> = mapOf("str" to 5, "dex" to 5, "int" to 3, "hp" to 20)
}
