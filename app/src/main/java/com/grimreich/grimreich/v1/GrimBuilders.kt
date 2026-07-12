package com.grimreich.grimreich.v1

import java.util.UUID

object GrimBuilders {
    fun randomId(prefix: String): String = "$prefix-${UUID.randomUUID()}"
    fun grimWorld(id: String = randomId("world"), name: String = "Grimreich", regions: List<Region> = listOf(region()), factions: List<Faction> = listOf(faction()), notes: String? = null) = GrimWorld(id, name, regions.toList(), factions.toList(), notes)
    fun region(id: String = randomId("region"), name: String = "Misty Vale", description: String = "A foggy, half-ruined city where shadows move.", encounters: List<Encounter> = listOf(encounter()), seed: Long? = null) = Region(id, name, description, encounters.toList(), seed)
    
    fun npc(
        id: String = randomId("npc"), 
        name: String = "Unnamed", 
        role: String = "villager", 
        factionId: String? = null, 
        personality: String = "Normal",
        stats: Map<String, Int> = defaultStats(), 
        inventory: List<Item> = emptyList(), 
        dialogue: Map<String, Any>? = null
    ) = NPC(id, name, role, factionId, personality, stats.toMap(), inventory.toList(), dialogue)

    fun basicNPCLifePath(name: String) = NPCLifePath(name, "mist", "blood", "reflection", 1f, 1f, listOf("birth"), "npc", "ending")
    fun emptyWorldChronicle(name: String) = WorldChronicle(name, "mist", "blood", "reflection", "fullness", "chaos", "region", "npc", "ending")
    fun defaultNonlinearTime(regionName: String) = NonlinearTime(regionName, 1, 1, 1, 1, 1, emptyList(), "region", "npc", "monster", "ending")
    fun boss(id: String = randomId("boss"), name: String = "Ancient Horror", level: Int = 10, lootTable: RewardTable = rewardTable()) = Boss(id, name, level, lootTable)
    
    fun item(
        instanceId: String = randomId("inst"), 
        templateId: String = randomId("tmpl"), 
        name: String = "Rusty Blade", 
        type: String = "weapon", 
        slot: String? = null,
        value: Int = 10,
        weight: Double = 1.0,
        rarity: String = "common", 
        lore: String = "",
        properties: Map<String, Any> = emptyMap(),
        effects: Map<String, Int> = emptyMap()
    ) = Item(instanceId = instanceId, templateId = templateId, name = name, type = type, slot = slot, value = value, weight = weight, rarity = rarity, lore = lore, properties = properties.toMap(), effects = effects)

    fun lootEntry(itemId: String, weight: Int = 10, minQty: Int = 1, maxQty: Int = 1) = LootEntry(itemId, weight, minQty, maxQty)
    fun rewardTable(id: String = randomId("reward"), entries: List<LootEntry> = listOf(lootEntry(item().templateId, 100))) = RewardTable(id, entries.toList())
    fun encounter(id: String = randomId("enc"), name: String = "Wandering Bandits", difficulty: Int = 1, possibleNpcs: List<String> = emptyList()) = Encounter(id, name, difficulty, possibleNpcs.toList())
    fun faction(id: String = randomId("faction"), name: String = "Order of the Candle", disposition: String = "neutral") = Faction(id, name, disposition)
    fun quest(id: String = randomId("quest"), title: String = "A Small Favor", description: String = "Help the tavern keeper.", rewards: RewardTable = rewardTable()) = Quest(id, title, description, rewards)
    fun skill(id: String = randomId("skill"), name: String = "Strike", power: Int = 5) = Skill(id, name, power)
    fun equipment(id: String = randomId("equip"), name: String = "Leather Cap", slot: String = "head", stats: Map<String, Int> = mapOf("def" to 1)) = Equipment(id, name, slot, stats.toMap())
    private fun defaultStats(): Map<String, Int> = mapOf("str" to 5, "dex" to 5, "int" to 3, "hp" to 20)

    fun defaultRegionConsciousness(name: String) = RegionConsciousness(name, "mist", "blood", "reflection", "neutral", emptyList(), emptyList(), "none", "none")
    fun defaultFullnessArchitecture(name: String) = FullnessArchitecture(name, "mist", "blood", "reflection", "none", "none", "none", "none")
    fun defaultFullnessAvatar(name: String) = FullnessAvatar(name, "mist", "blood", "reflection", 1f, 1f, emptyList(), "none", "none", "none")
    fun defaultAbsoluteMutation(name: String) = AbsoluteMutation(name, 1, 1, 1, 1, emptyList(), "none", "none", "none", "none")
    fun defaultAlternateHistory(name: String) = AlternateHistory(name, "mist", "blood", "reflection", "none", "none", "none", "none", "none")
    fun defaultOtherSideExpedition(name: String, log: String? = null, sym: String? = null, zero: String? = null, enemies: List<String>? = null, rewards: List<String>? = null, impact: String? = null, ending: String? = null) = OtherSideExpedition(name, log ?: "log", sym ?: "sym", zero ?: "zero", enemies ?: emptyList(), rewards ?: emptyList(), impact ?: "none", ending ?: "none")
    fun defaultPhenomenonReligion(name: String, phenom: String? = null, dogma: String? = null, rituals: List<String>? = null, prophets: List<String>? = null, artifacts: List<String>? = null, impact: String? = null, npc: String? = null, ending: String? = null) = PhenomenonReligion(name, phenom ?: "phenom", dogma ?: "dogma", rituals ?: emptyList(), prophets ?: emptyList(), artifacts ?: emptyList(), impact ?: "none", npc ?: "none", ending ?: "none")
    fun defaultFullnessArtifact(name: String) = FullnessArtifact(name, "mist", "blood", "reflection", "none", "none", "none", "none", "none")
    fun defaultTriLayerRelationship(name: String) = TriLayerRelationship(name, "mist", "blood", "reflection", 1f, 1f, "none", "none", "none")
    fun defaultWorldCollapse(stage: String? = null, loss: Int? = null, layer: Int? = null, region: Int? = null, npc: Int? = null, monster: Int? = null, history: Int? = null, ending: String? = null) = WorldCollapse(stage ?: "stage", loss ?: 1, layer ?: 1, region ?: 1, npc ?: 1, monster ?: 1, history ?: 1, ending ?: "none")
    
    val northCoastConsciousness get() = defaultRegionConsciousness("Wybrzeże Północne")
    val northCoastTime get() = defaultNonlinearTime("Wybrzeże Północne")
    
    fun aelion() = basicNPCLifePath("Aelion")
    
    fun allRegions() = listOf("Wybrzeże Północne", "Serce Krainy")

    @JvmStatic fun northCoastConsciousness() = northCoastConsciousness
    @JvmStatic fun northCoastTime() = northCoastTime
}
