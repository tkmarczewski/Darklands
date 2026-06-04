package com.darklandsmobile.grimreich.v1

import kotlin.random.Random

data class GrimQuest(val id:String,val title:String,val description:String,val regionName:String,val relatedNpc:String?,val phenomenon:String,val difficulty:Int,val rewards:List<String>)
data class GrimWorldEvent(val id:String,val regionName:String,val title:String,val description:String,val impactOnReputation:Int,val impactOnCollapse:Int)
data class GeneratedLoot(val entries: List<LootEntry>)

class NpcGenerator(private val random: Random = Random.Default) { fun generateNpc(regionName:String, phenomenon:String, power:Int): NPCLifePath = NPCLifePath("${regionName.take(4)}-${random.nextInt(100,999)}","los_$phenomenon","ciało_$phenomenon","dusza_$phenomenon",power.coerceAtMost(10),(power+1).coerceAtMost(10), listOf("narodziny","pęknięcie","rozdarcie"), "impact_$regionName", "ending_$phenomenon") }
class QuestGenerator(private val random: Random = Random.Default) { fun generateQuest(regionName:String, phenomenon:String, npc:NPCLifePath?, difficulty:Int): GrimQuest = GrimQuest("q_${random.nextInt(1000,9999)}","Zadanie: $regionName","Quest oparty o $phenomenon",regionName,npc?.npcName,phenomenon,difficulty,listOf("loot","echo")) }
class WorldEventGenerator(private val random: Random = Random.Default) { fun generate(region:RegionConsciousness, time:NonlinearTime, collapse:WorldCollapse): GrimWorldEvent = GrimWorldEvent("e_${random.nextInt(1000,9999)}", region.regionName, "Zdarzenie: ${region.regionName}", "${region.emotionalState} / ${time.regionImpact}",-1, collapse.phenomenonLoss + collapse.layerCollapse) }
class ExpeditionGenerator(private val random: Random = Random.Default) { fun generate(regionName:String, phenomenon:String, chaosLevel:Int, difficulty:Int): OtherSideExpedition = OtherSideExpedition("Druga Strona: $regionName","Logic-$regionName","Sym-$regionName","Zero-$regionName", listOf("shadow_wraith","mirror_knight"), listOf("mist_shard","blood_seal"), "impact_$regionName", "ending_$phenomenon", difficulty) }
class LootRoller(private val random: Random = Random.Default) { fun roll(baseRewards:List<String>, summary:OtherSideReward, rolls:Int): GeneratedLoot = GeneratedLoot(List(rolls.coerceAtLeast(1)) { LootEntry(baseRewards.ifEmpty { listOf("scrap") }.random(random)) }) }
