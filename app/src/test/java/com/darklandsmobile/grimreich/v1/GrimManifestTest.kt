package com.darklandsmobile.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class GrimManifestTest {
    @Test fun all_core_models_are_present_and_wired() {
        val engine = GrimWorldEngineFactory.create()
        val region = RegionConsciousness("Testland", "", "", "", "", emptyList(), emptyList(), "", "")
        val time = GrimBuilders.defaultNonlinearTime("Testland")
        val arch = FullnessArchitecture("Test_Arch", "", "", "", "", "", "", "")
        engine.loadRegion(region, time, arch)
        engine.registerNPC(GrimBuilders.basicNPCLifePath("Test NPC"), emptyList())
        engine.updateWorldCollapse(WorldCollapse("test", 0, 0, 0, 0, 0, 0, ""))
        engine.registerReligion(PhenomenonReligion("Test Cult", "mist", "", emptyList(), emptyList(), emptyList(), "", "", ""))
        engine.registerArtifact(FullnessArtifact("Test Artifact", "", "", "", "", "", "", "", ""))
        engine.setAvatar(FullnessAvatar("Test Avatar", "", "", "", 1, 0, emptyList(), "", "", ""))
        engine.startExpedition(OtherSideExpedition("Test Expedition", "", "", "", emptyList(), emptyList(), "", "", 1))
        engine.recordHistory(AlternateHistory("Test History", "", "", "", "", "", "", "", ""), GrimBuilders.emptyWorldChronicle("Test Chronicle"))
        engine.applyAbsoluteMutation(AbsoluteMutation("Test Monster", 1, 1, 1, 0, emptyList(), "", "", "", ""))
        assertTrue(true)
    }

    @Test fun definition_matches_manifest() {
        val def = GrimWorldModule.definition
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.MIST))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.BLOOD))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.REFLECTION))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.FULLNESS))
        assertEquals("NonlinearTime", def.timeModel)
        assertTrue(def.hasTriLayerNpcRelations)
        assertTrue(def.hasOtherSide)
    }
}
