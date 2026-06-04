package com.darklandsmobile.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class GrimManifestTest {

    @Test
    fun all_core_models_are_present_and_wired() {
        val engine = GrimWorldEngineFactory.create()

        val region = RegionConsciousness(
            regionName = "Testland",
            mistMind = "",
            bloodBody = "",
            reflectionSoul = "",
            emotionalState = "",
            memory = emptyList(),
            reactions = emptyList(),
            regionImpact = "",
            endingImpact = ""
        )
        val time = GrimBuilders.defaultNonlinearTime("Testland")
        val arch = FullnessArchitecture(
            structureName = "Test_Arch",
            mistForm = "",
            bloodForm = "",
            reflectionForm = "",
            fullnessEffect = "",
            chaosEffect = "",
            regionImpact = "",
            endingImpact = ""
        )

        engine.loadRegion(region, time, arch)

        val npc = GrimBuilders.basicNPCLifePath("Test NPC")
        engine.registerNPC(npc, emptyList())

        val mut = AbsoluteMutation(
            targetName = "Test Monster",
            zeroMindLevel = 1,
            zeroBodyLevel = 1,
            zeroSoulLevel = 1,
            worldErasureLevel = 0,
            activeEffects = emptyList(),
            regionImpact = "",
            npcImpact = "",
            monsterImpact = "",
            endingImpact = ""
        )
        engine.applyAbsoluteMutation(mut)

        val hist = AlternateHistory(
            historyName = "Test History",
            mistVersion = "",
            bloodVersion = "",
            reflectionVersion = "",
            fullnessImpact = "",
            chaosImpact = "",
            regionImpact = "",
            npcImpact = "",
            endingImpact = ""
        )
        val chron = GrimBuilders.emptyWorldChronicle("Test Chronicle")
        engine.recordHistory(hist, chron)

        val relig = PhenomenonReligion(
            religionName = "Test Cult",
            phenomenon = "mist",
            dogma = "",
            rituals = emptyList(),
            prophets = emptyList(),
            artifacts = emptyList(),
            regionImpact = "",
            npcImpact = "",
            endingImpact = ""
        )
        engine.registerReligion(relig)

        val artifact = FullnessArtifact(
            artifactName = "Test Artifact",
            mistEffect = "",
            bloodEffect = "",
            reflectionEffect = "",
            fullnessEffect = "",
            chaosEffect = "",
            regionImpact = "",
            npcImpact = "",
            endingImpact = ""
        )
        engine.registerArtifact(artifact)

        val avatar = FullnessAvatar(
            avatarName = "Test Avatar",
            mistForm = "",
            bloodForm = "",
            reflectionForm = "",
            fullnessLevel = 1,
            chaosLevel = 0,
            abilities = emptyList(),
            regionImpact = "",
            npcImpact = "",
            endingImpact = ""
        )
        engine.setAvatar(avatar)

        val exp = OtherSideExpedition(
            expeditionName = "Test Expedition",
            logicalLayer = "",
            symmetricLayer = "",
            zeroLayer = "",
            enemies = emptyList(),
            rewards = emptyList(),
            regionImpact = "",
            endingImpact = "",
            difficultyTier = 1
        )
        engine.startExpedition(exp)

        val collapse = WorldCollapse(
            collapseStage = "test",
            phenomenonLoss = 0,
            layerCollapse = 0,
            regionDecay = 0,
            npcDecay = 0,
            monsterDecay = 0,
            historyLoss = 0,
            endingImpact = ""
        )
        engine.updateWorldCollapse(collapse)

        assertTrue(true)
    }

    @Test
    fun definition_matches_manifest() {
        val def = GrimWorldModule.definition
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.MIST))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.BLOOD))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.REFLECTION))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.FULLNESS))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.RIFT))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.ABSOLUTE))
        assertEquals("NonlinearTime", def.timeModel)
        assertTrue(def.hasTriLayerNpcRelations)
        assertTrue(def.hasOtherSide)
    }
}
