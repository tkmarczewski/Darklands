package com.darklandsmobile.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class GrimGeneratorsTest {
    @Test fun can_generate_npcs_quests_events_expeditions_and_loot() {
        val npc = NpcGenerator().generateNpc("Wybrzeże Północne", "Mgła", 4)
        val quest = QuestGenerator().generateQuest("Serce Krainy", "Odbicie", npc, 3)
        val event = WorldEventGenerator().generate(
            RegionConsciousness("R","m","b","r","a", listOf("mem"), listOf("react"), "ri", "ei"),
            NonlinearTime("R",1,1,1,1,1, emptyList(), "ri", "ni", "mi", "ei"),
            WorldCollapse("stage",1,1,0,0,0,0,"ei")
        )
        val exp = ExpeditionGenerator().generate("Równiny Koronne", "Krew", 5, 2)
        val loot = LootRoller().roll(listOf("x"), OtherSideReward(1,1,1), 2)
        assertTrue(npc.npcName.isNotBlank())
        assertTrue(quest.title.isNotBlank())
        assertTrue(event.title.isNotBlank())
        assertTrue(exp.enemies.isNotEmpty())
        assertEquals(2, loot.entries.size)
    }

    @Test fun campaign_generator_builds_full_package() {
        val pack = GrimCampaignGenerator().generateRegionPackage(
            RegionConsciousness("R","m","b","r","a", listOf("mem"), listOf("react"), "ri", "ei"),
            NonlinearTime("R",1,1,1,1,1, emptyList(), "ri", "ni", "mi", "ei"),
            WorldCollapse("stage",1,1,0,0,0,0,"ei"),
            "mist"
        )
        assertEquals(3, pack.npcs.size)
        assertEquals(3, pack.quests.size)
        assertEquals(2, pack.events.size)
        assertEquals(2, pack.expeditions.size)
    }
}
