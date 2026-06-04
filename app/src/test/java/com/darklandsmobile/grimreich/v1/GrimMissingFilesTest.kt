package com.darklandsmobile.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class GrimMissingFilesTest {
    @Test fun catalogues_and_generators_exist() {
        assertEquals(7, GrimRegionCatalogue.all.size)
        assertTrue(GrimBossCatalogue.allRegionalBosses.isNotEmpty())
        assertTrue(GrimNpcCatalogue.all.isNotEmpty())
        val npc = NpcGenerator().generateNpc("Nowy Świat", "mist", 4)
        val q = QuestGenerator().generateQuest("Nowy Świat", "mist", npc, 2)
        val ev = WorldEventGenerator().generateEvent(
            RegionConsciousness("R","m","b","r","a", listOf("mem"), listOf("react"), "ri", "ei"),
            NonlinearTime("R",1,1,1,1,1, emptyList(), "ri", "ni", "mi", "ei"),
            WorldCollapse("stage",1,1,0,0,0,0,"ei")
        )
        val ex = ExpeditionGenerator().generateExpedition("Nowy Świat", "mist", 3, 2)
        val loot = LootRoller().roll(listOf("x"), OtherSideReward(1,1,1), 2)
        assertTrue(npc.npcName.isNotBlank())
        assertTrue(q.title.isNotBlank())
        assertTrue(ev.title.isNotBlank())
        assertTrue(ex.enemies.isNotEmpty())
        assertEquals(2, loot.entries.size)
    }
}
