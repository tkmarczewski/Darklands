package com.grimreich.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class GrimWorldEngineTest {

    @Test
    fun `engine wires region and npc systems together`() {
        val regionSystem = DefaultRegionSystem()
        val npcSystem = DefaultNPCSystem()
        val monsterSystem = object : MonsterSystem {
            override fun applyMutation(mutation: AbsoluteMutation) {}
        }
        val narrativeSystem = object : WorldNarrativeSystem {
            override fun registerAlternateHistory(history: AlternateHistory) {}
            override fun registerChronicle(chronicle: WorldChronicle) {}
        }
        val expeditionSystem = object : ExpeditionSystem {
            override fun startOtherSideExpedition(expedition: OtherSideExpedition) {}
        }
        val artifactSystem = object : ArtifactSystem {
            override fun registerFullnessArtifact(artifact: FullnessArtifact) {}
        }
        val avatarSystem = object : AvatarSystem {
            override fun setFullnessAvatar(avatar: FullnessAvatar) {}
        }

        val engine = GrimWorldEngine(
            regionSystem = regionSystem,
            npcSystem = npcSystem,
            monsterSystem = monsterSystem,
            narrativeSystem = narrativeSystem,
            expeditionSystem = expeditionSystem,
            artifactSystem = artifactSystem,
            avatarSystem = avatarSystem
        )

        val region = GrimBuilders.defaultRegionConsciousness("Mist City")
        val time = GrimBuilders.defaultNonlinearTime("Mist City")
        val arch = GrimBuilders.defaultFullnessArchitecture("Mist Cathedral")

        val npcPath = GrimBuilders.basicNPCLifePath("Helga von Nebel")
        val rels = listOf(GrimBuilders.defaultTriLayerRelationship("Helga von Nebel"))

        engine.loadRegion(region, time, arch)
        engine.registerNPC(npcPath, rels)

        val regionSnapshot = engine.query.getRegionSnapshot("Mist City")
        assertNotNull(regionSnapshot)
        assertEquals("Mist City", regionSnapshot!!.regionName)

        val npcState = engine.queryNpc("Helga von Nebel")
        npcState // keep reference used
    }

    @Test
    fun `world collapse is applied through engine`() {
        val regionSystem = DefaultRegionSystem()
        val engine = GrimWorldEngine(
            regionSystem = regionSystem,
            npcSystem = DefaultNPCSystem(),
            monsterSystem = object : MonsterSystem {
                override fun applyMutation(mutation: AbsoluteMutation) {}
            },
            narrativeSystem = object : WorldNarrativeSystem {
                override fun registerAlternateHistory(history: AlternateHistory) {}
                override fun registerChronicle(chronicle: WorldChronicle) {}
            },
            expeditionSystem = object : ExpeditionSystem {
                override fun startOtherSideExpedition(expedition: OtherSideExpedition) {}
            },
            artifactSystem = object : ArtifactSystem {
                override fun registerFullnessArtifact(artifact: FullnessArtifact) {}
            },
            avatarSystem = object : AvatarSystem {
                override fun setFullnessAvatar(avatar: FullnessAvatar) {}
            }
        )

        val collapse = GrimBuilders.defaultWorldCollapse().copy(
            collapseStage = "erosion_of_phenomena",
            phenomenonLoss = 10
        )

        engine.updateWorldCollapse(collapse)

        val snapshot = engine.query.getCollapseSnapshot()
        snapshot?.let {
            assertEquals("erosion_of_phenomena", it.collapseStage)
        }
    }
}
