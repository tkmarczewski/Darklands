package com.darklandsmobile.grimreich.v1

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class GrimGeneratorsTest {
 @Test fun npcGeneratorProducesDifferentNames(){ val g=NpcGenerator(Random(1)); val a=g.generateNpc("Nowe Wybrzeże","mist",5); val b=g.generateNpc("Nowe Wybrzeże","mist",5); assertNotEquals(a.npcName,b.npcName) }
 @Test fun questGeneratorUsesNpcAndRegion(){ val g=QuestGenerator(Random(2)); val npc=GrimNpcCatalogue.aelion; val q=g.generateQuest("Wybrzeże","reflection",npc,4); assertTrue(q.description.contains("Aelion")); assertTrue(q.description.contains("Wybrzeże")) }
 @Test fun worldEventGeneratorRespondsToCollapse(){ val g=WorldEventGenerator(Random(3)); val region=GrimBuilders.defaultRegionConsciousness("Północne Wybrzeże").copy(emotionalState="niepokój", memory=listOf("burza","zaginiony port")); val time=GrimBuilders.defaultNonlinearTime("Północne Wybrzeże"); val collapse=WorldCollapse("erosion",8,2,0,0,0,0,""); val e=g.generateEvent(region,time,collapse); assertTrue(e.title.isNotBlank()); assertTrue(e.impactOnCollapse>0) }
 @Test fun expeditionGeneratorSetsPhenomenonSpecificFields(){ val g=ExpeditionGenerator(Random(4)); val e=g.generateExpedition("Wybrzeże","mist",6,3); assertTrue(e.logicalLayer.contains("Mist")); assertTrue(e.rewards.isNotEmpty()); assertTrue(e.difficultyTier>=1) }
}
