package com.darklandsmobile.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class GrimFullSuiteTest {

    @Test fun module_definition_is_complete() {
        assertTrue(GrimWorldModule.definition.corePhenomena.isNotEmpty())
        assertTrue(GrimWorldModule.definition.hasOtherSide)
        assertTrue(GrimWorldModule.definition.hasTriLayerNpcRelations)
        assertEquals("NonlinearTime", GrimWorldModule.definition.timeModel)
    }

    @Test fun catalogues_have_core_content() {
        assertEquals(7, GrimRegionCatalogue.regions.size)
        assertTrue(GrimBossCatalogue.allBosses.isNotEmpty())
        assertTrue(GrimNpcCatalogue.all.isNotEmpty())
    }

    @Test fun builders_cover_empty_defaults() {
        val time = GrimBuilders.defaultNonlinearTime("X")
        val npc = GrimBuilders.basicNPCLifePath("Y")
        val chronicle = WorldChronicle("Z", "m", "b", "r", "f", "c", "ri", "ni", "ei")
        assertEquals("X", time.regionName)
        assertEquals("Y", npc.npcName)
        assertEquals("Z", chronicle.chronicleName)
    }

    @Test fun engine_supports_full_lifecycle() {
        val engine = GrimWorldEngineFactory.create()
        val region = RegionConsciousness("Testland", "mist", "blood", "reflection", "calm", listOf("mem1", "mem2"), listOf("react1"), "impact", "ending")
        val time = NonlinearTime("Testland", 1, 2, 3, 4, 5, listOf("slow"), "region", "npc", "monster", "ending")
        val arch = FullnessArchitecture("Citadel", "m", "b", "r", "f", "c", "region", "ending")
        val npc = NPCLifePath("NPC", "m", "b", "r", 1, 2, listOf("t1"), "impact", "ending")
        val rel = TriLayerRelationship("NPC", "m1", "b1", "r1", 2, 3, "emo", "region", "ending")
        val religion = PhenomenonReligion("Cult", "mist", "dogma", listOf("ritual"), listOf("prophet"), listOf("artifact"), "region", "npc", "ending")
        val artifact = FullnessArtifact("Relic", "m", "b", "r", "f", "c", "region", "npc", "ending")
        val avatar = FullnessAvatar("Avatar", "m", "b", "r", 5, 6, listOf("phase"), "region", "npc", "ending")
        val expedition = OtherSideExpedition("Expedition", "L", "S", "Z", listOf("enemy"), listOf("reward"), "region", "ending", 2)
        val collapse = WorldCollapse("stage", 1, 2, 3, 4, 5, 6, "ending")
        val history = AlternateHistory("History", "m", "b", "r", "f", "c", "region", "npc", "ending")
        val chronicle = WorldChronicle("Chronicle", "m", "b", "r", "f", "c", "region", "npc", "ending")
        val mutation = AbsoluteMutation("Monster", 1, 2, 3, 4, listOf("trait"), "region", "npc", "monster", "ending")
        engine.loadRegion(region, time, arch)
        engine.registerNPC(npc, listOf(rel))
        engine.registerReligion(religion)
        engine.registerArtifact(artifact)
        engine.setAvatar(avatar)
        engine.startExpedition(expedition)
        engine.updateWorldCollapse(collapse)
        engine.recordHistory(history, chronicle)
        engine.applyAbsoluteMutation(mutation)
        assertNotNull(engine.query.getRegionSnapshot("Testland"))
        assertNotNull(engine.queryNpc("NPC"))
        assertNull(engine.query.queryReligion("Cult"))
        assertNull(engine.query.queryArtifact("Relic"))
        assertNull(engine.query.queryAvatar())
        assertNull(engine.query.queryExpedition("Expedition"))
        assertNotNull(engine.query.queryCollapse())
        assertNull(engine.query.queryHistory("History"))
        assertNull(engine.query.queryChronicle("Chronicle"))
    }

    @Test fun generators_cover_full_content_flow() {
        val region = RegionConsciousness("R", "m", "b", "r", "a", listOf("mem"), listOf("react"), "impact", "ending")
        val time = NonlinearTime("R", 1, 1, 1, 1, 1, emptyList(), "region", "npc", "monster", "ending")
        val collapse = WorldCollapse("stage", 0, 1, 2, 3, 4, 5, "ending")
        val npc = NpcGenerator().generateNpc("R", "mist", 3)
        val quest = QuestGenerator().generateQuest("R", "mist", npc, 2)
        val event = WorldEventGenerator().generate(region, time, collapse)
        val expedition = ExpeditionGenerator().generate("R", "mist", 4, 2)
        val loot = LootRoller().roll(listOf("x"), OtherSideReward(emptyList(), emptyList(), ""), 3)
        val pack = GrimCampaignGenerator.generateRegionPackage(region, time, collapse, "mist")
        assertTrue(npc.npcName.isNotBlank())
        assertTrue(quest.title.isNotBlank())
        assertTrue(event.title.isNotBlank())
        assertTrue(expedition.enemies.isNotEmpty())
        assertEquals(3, loot.entries.size)
        assertEquals(3, pack.npcs.size)
        assertEquals(1, pack.quests.size)
        assertEquals(2, pack.events.size)
        assertEquals(2, pack.expeditions.size)
    }

    @Test fun deep_manifest_logic_is_covered() {
        val def = GrimWorldModule.definition
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.MIST))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.BLOOD))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.REFLECTION))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.FULLNESS))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.RIFT))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.ABSOLUTE))
    }
}
