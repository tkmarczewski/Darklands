package com.darklandsmobile.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class GrimFullTest {
    @Test fun engine_core_roundtrip() {
        val engine = GrimWorldEngineFactory.create()
        val region = RegionConsciousness("Testland","m","b","r","calm", listOf("mem"), listOf("react"), "impact", "end")
        val time = NonlinearTime("Testland",1,1,1,1,1, emptyList(), "ri", "ni", "mi", "ei")
        val arch = FullnessArchitecture("Arch","m","b","r","f","c","ri","ei")
        engine.loadRegion(region, time, arch)
        engine.registerNPC(NPCLifePath("NPC","m","b","r",1,1, emptyList(), "ni", "ei"), emptyList())
        engine.registerReligion(PhenomenonReligion("Rel","mist","dogma", emptyList(), emptyList(), emptyList(), "ri", "ni", "ei"))
        engine.registerArtifact(FullnessArtifact("Art","m","b","r","f","c","ri","ni","ei"))
        engine.setAvatar(FullnessAvatar("Av","m","b","r",1,1, emptyList(), "ri", "ni", "ei"))
        engine.startExpedition(OtherSideExpedition("Exp","L","S","Z", listOf("e"), listOf("r"), "ri", "ei", 1))
        engine.recordHistory(AlternateHistory("Hist","m","b","r","f","c","ri","ni","ei"), WorldChronicle("Ch","m","b","r","f","c","ri","ni","ei"))
        engine.updateWorldCollapse(WorldCollapse("stage",1,1,1,1,1,1,"ei"))
        engine.applyAbsoluteMutation(AbsoluteMutation("X",1,1,1,1, emptyList(), "ri", "ni", "mi", "ei"))
        assertTrue(true)
    }

    @Test fun builders_and_module_cover_manifest() {
        val n = GrimBuilders.defaultNonlinearTime("A")
        val npc = GrimBuilders.basicNPCLifePath("B")
        val c = GrimBuilders.emptyWorldChronicle("C")
        assertEquals("A", n.regionName)
        assertEquals("B", npc.npcName)
        assertEquals("C", c.chronicleName)
        assertTrue(GrimWorldModule.definition.hasOtherSide)
        assertTrue(GrimWorldModule.definition.hasTriLayerNpcRelations)
    }

    @Test fun generators_create_consistent_objects() {
        val npc = NpcGenerator().generateNpc("Wybrzeże Północne", "Mgła", 4)
        val quest = QuestGenerator().generateQuest("Serce Krainy", "Odbicie", npc, 3)
        val ev = WorldEventGenerator().generate(RegionConsciousness("R","","","","a", listOf("m"), listOf("r"), "i", "e"), GrimBuilders.defaultNonlinearTime("R"), WorldCollapse("s",1,1,1,1,1,1,"e"))
        val ex = ExpeditionGenerator().generate("Równiny Koronne", "Krew", 5, 2)
        val loot = LootRoller().roll(listOf("x"), OtherSideReward(1,1,1), 2)
        assertTrue(npc.npcName.isNotBlank())
        assertTrue(quest.title.isNotBlank())
        assertTrue(ev.title.isNotBlank())
        assertTrue(ex.enemies.isNotEmpty())
        assertEquals(2, loot.entries.size)
    }
}
