package com.darklandsmobile.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class GrimClosingManifestTest {
    @Test fun new_lore_catalogues_are_present() {
        assertTrue(GrimRegionCatalogue.allRegions.size == 7)
        assertTrue(GrimBossCatalogue.allBosses.isNotEmpty())
        assertTrue(GrimNpcCatalogue.all.isNotEmpty())
    }
    @Test fun generators_work_with_new_lore() {
        val npc = NpcGenerator().generateNpc("Wybrzeże Północne","mist",5)
        val quest = QuestGenerator().generateQuest("Serce Krainy","reflection",npc,4)
        val region = GrimBuilders.northCoastConsciousness
        val event = WorldEventGenerator().generateEvent(region, GrimBuilders.northCoastTime, WorldCollapse("collapse",1,1,0,0,0,0,""))
        val exp = ExpeditionGenerator().generateExpedition("Ziemie Dzikie","blood",6,3)
        val loot = LootRoller().roll(listOf("mistshard"), OtherSideReward(emptyList(), emptyList(), ""), 3)
        assertTrue(quest.title.isNotBlank())
        assertTrue(event.title.isNotBlank())
        assertTrue(exp.enemies.isNotEmpty())
        assertTrue(loot.entries.isNotEmpty())
    }
}
