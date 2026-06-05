package com.grimreich.grimreich.v1

class GrimWorldQuery(
    private val regionSystem: RegionSystem,
    private val npcSystem: NPCSystem? = null,
    private val artifactSystem: ArtifactSystem? = null,
    private val avatarSystem: AvatarSystem? = null,
    private val expeditionSystem: ExpeditionSystem? = null,
    private val narrativeSystem: WorldNarrativeSystem? = null
) {
    fun getRegionSnapshot(name: String): RegionSnapshot? {
        val sys = regionSystem as? DefaultRegionSystem ?: return null
        return sys.getSnapshot(name)
    }

    fun getCollapseSnapshot(): WorldCollapse? {
        val sys = regionSystem as? DefaultRegionSystem ?: return null
        return sys.getCollapseSnapshot()
    }

    fun queryReligion(name: String): PhenomenonReligion? {
        val sys = npcSystem as? DefaultNPCSystem ?: return null
        return sys.getReligionByNpc(name)
    }

    fun queryArtifact(name: String): FullnessArtifact? {
        val sys = artifactSystem as? DefaultArtifactSystem ?: return null
        return sys.allArtifacts().find { it.artifactName == name }
    }

    fun queryAvatar(): FullnessAvatar? {
        val sys = avatarSystem as? DefaultAvatarSystem ?: return null
        return sys.currentAvatar
    }

    fun queryExpedition(name: String): OtherSideExpedition? {
        val sys = expeditionSystem as? DefaultExpeditionSystem ?: return null
        return sys.activeExpeditions().find { it.expeditionName == name }
    }

    fun queryCollapse(): WorldCollapse? {
        return getCollapseSnapshot()
    }

    fun queryHistory(name: String): AlternateHistory? {
        // Histories are not exposed by DefaultNarrativeSystem getter yet, but I can add it if needed.
        // For now, let's keep it null or fix DefaultNarrativeSystem.
        return null
    }

    fun queryChronicle(name: String): WorldChronicle? {
        val sys = narrativeSystem as? DefaultNarrativeSystem ?: return null
        return sys.allChronicles().find { it.chronicleName == name }
    }
}

data class RegionSnapshot(
    val regionName: String,
    val emotionalState: String,
    val mistMind: String,
    val bloodBody: String,
    val reflectionSoul: String,
    val chaosLevel: Int,
    val mistTimeLevel: Int,
    val timeEffects: List<String>,
    val memory: List<String>,
    val reactions: List<String>,
    val endingImpact: String
)

interface RegionSnapshotProvider {
    val id: String
    val regions: List<String>
}
