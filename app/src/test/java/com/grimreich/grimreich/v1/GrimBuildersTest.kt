package com.grimreich.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class GrimBuildersTest {
    @Test fun buildersCoverAllModels() {
        assertEquals("A", GrimBuilders.defaultNonlinearTime("A").regionName)
        assertEquals("NPC", GrimBuilders.basicNPCLifePath("NPC").npcName)
        assertEquals("C", GrimBuilders.emptyWorldChronicle("C").chronicleName)
        assertEquals("R", GrimBuilders.defaultRegionConsciousness("R").regionName)
        assertEquals("A", GrimBuilders.defaultFullnessArchitecture("A").structureName)
        assertEquals("X", GrimBuilders.defaultFullnessAvatar("X").avatarName)
        assertEquals("H", GrimBuilders.defaultAbsoluteMutation("H").targetName)
        assertEquals("H", GrimBuilders.defaultAlternateHistory("H").historyName)
        assertEquals("E", GrimBuilders.defaultOtherSideExpedition("E", "R").expeditionName)
        assertEquals("R", GrimBuilders.defaultPhenomenonReligion("R", "mist").religionName)
        assertEquals("A", GrimBuilders.defaultFullnessArtifact("A").artifactName)
        assertEquals("N", GrimBuilders.defaultTriLayerRelationship("N").npcName)
        assertEquals("S", GrimBuilders.defaultWorldCollapse("S").collapseStage)
    }
}
