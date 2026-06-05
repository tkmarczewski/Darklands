package com.grimreich.grimreich.v1

import kotlin.random.Random

data class GrimQuest(val id:String,val title:String,val description:String,val regionName:String,val relatedNpc:String?,val phenomenon:String,val difficulty:Int,val rewards:List<String>)
data class GrimWorldEvent(val id:String,val regionName:String,val title:String,val description:String,val impactOnReputation:Int,val impactOnCollapse:Int)

class NpcGenerator(private val random: Random = Random.Default) { 
    fun generateNpc(regionName:String, phenomenon:String, power:Int): NPCLifePath = NPCLifePath("${regionName.take(4)}-${random.nextInt(100,999)}","los_$phenomenon","ciało_$phenomenon","dusza_$phenomenon",power.coerceAtMost(10),(power+1).coerceAtMost(10), listOf("narodziny","pęknięcie","rozdarcie"), "impact_$regionName", "ending_$phenomenon") 
    @JvmName("aelion_member") fun aelion() = generateNpc("Wybrzeże Północne", "Mgła", 5)
}
class QuestGenerator(private val random: Random = Random.Default) { 
    fun generateQuest(regionName:String, phenomenon:String, npc:NPCLifePath?, difficulty:Int): GrimQuest = GrimQuest("q_${random.nextInt(1000,9999)}","Zadanie: $regionName","Quest oparty o $phenomenon",regionName,npc?.npcName,phenomenon,difficulty,listOf("loot","echo")) 
    fun generateCampaign() = listOf(generateQuest("Region", "Phenom", null, 1))
    fun generateRegionPackage(name: String) = RegionPackage(generateCampaign(), emptyList(), emptyList(), emptyList())
    fun generateRegionPackage(region: RegionConsciousness, time: NonlinearTime, collapse: WorldCollapse, phenomenon: String) = RegionPackage(generateCampaign(), listOf(NpcGenerator().aelion()), emptyList(), emptyList())
    fun generateRegionPackage(region: RegionConsciousness, time: NonlinearTime, collapse: WorldCollapse, phenomenon: GrimPhenomenon) = generateRegionPackage(region, time, collapse, phenomenon.name)
}
class WorldEventGenerator(private val random: Random = Random.Default) { 
    fun generate(region:RegionConsciousness, time:NonlinearTime, collapse:WorldCollapse): GrimWorldEvent = GrimWorldEvent("e_${random.nextInt(1000,9999)}", region.regionName, "Zdarzenie: ${region.regionName}", "${region.emotionalState} / ${time.regionImpact}",-1, collapse.phenomenonLoss + collapse.layerCollapse) 
    fun generateEvent(region: RegionConsciousness, time: NonlinearTime, collapse: WorldCollapse? = null, impact: Int? = null) = generate(region, time, collapse ?: GrimBuilders.defaultWorldCollapse())
}
class ExpeditionGenerator(private val random: Random = Random.Default) { 
    fun generate(regionName:String, phenomenon:String, chaosLevel:Int, difficulty:Int): OtherSideExpedition = OtherSideExpedition("Druga Strona: $regionName","Logic-$regionName","Sym-$regionName","Zero-$regionName", listOf("shadow_wraith","mirror_knight"), listOf("mist_shard","blood_seal"), "impact_$regionName", "ending_$phenomenon", difficulty) 
    fun generateExpedition(name: String, phenomenon: String? = null, chaos: Int? = null, difficulty: Int? = null, layers: List<String>? = null, enemies: List<String>? = null, rewards: List<String>? = null) = generate(name, phenomenon ?: "Phenom", chaos ?: 1, difficulty ?: 1)
}
class LootRoller(private val random: Random = Random.Default) { 
    fun roll(baseRewards:List<String>, summary:OtherSideReward, rolls:Int): GeneratedLoot = GeneratedLoot(List(rolls.coerceAtLeast(1)) { LootEntry(baseRewards.ifEmpty { listOf("scrap") }.random(random)) }) 
    fun roll(baseRewards:List<String>, summary:OtherSideReward, rolls:Int, tier: Int, chaos: Int): GeneratedLoot = roll(baseRewards, summary, rolls)
    fun roll(baseRewards:List<String>, summary:OtherSideReward, rolls:Int, tier: Int, chaos: Int, vararg other: Any): GeneratedLoot = roll(baseRewards, summary, rolls)
    fun roll(baseRewards:List<String>, summary:OtherSideReward, rolls:Int, tier: Int, chaos: Int, rare: Int, curse: Int, extra: String): GeneratedLoot = roll(baseRewards, summary, rolls)
}

object GrimCampaignGenerator {
    @JvmStatic fun generate() = QuestGenerator().generateCampaign()
    @JvmStatic fun generateRegionPackage(name: String) = QuestGenerator().generateRegionPackage(name)
    @JvmStatic fun generateRegionPackage(region: RegionConsciousness, time: NonlinearTime, collapse: WorldCollapse, phenomenon: String) = QuestGenerator().generateRegionPackage(region, time, collapse, phenomenon)
    @JvmStatic fun generateRegionPackage(region: RegionConsciousness, time: NonlinearTime, collapse: WorldCollapse, phenomenon: GrimPhenomenon) = QuestGenerator().generateRegionPackage(region, time, collapse, phenomenon)
    @JvmStatic fun aelion() = NpcGenerator().aelion()
}

data class RegionPackage(
    val quests: List<GrimQuest>,
    val npcs: List<NPCLifePath> = emptyList(),
    val events: List<GrimWorldEvent> = emptyList(),
    val expeditions: List<OtherSideExpedition> = emptyList()
)

fun aelion() = NpcGenerator().aelion()
