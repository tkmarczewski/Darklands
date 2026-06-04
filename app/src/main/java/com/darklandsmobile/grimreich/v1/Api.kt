package com.darklandsmobile.grimreich.v1

interface RegionSystem {
    fun applyConsciousness(region: RegionConsciousness)
    fun applyNonlinearTime(time: NonlinearTime)
    fun applyArchitecture(structure: FullnessArchitecture)
    fun applyCollapse(collapse: WorldCollapse)
}

interface NPCSystem {
    fun applyLifePath(path: NPCLifePath)
    fun applyRelationship(rel: TriLayerRelationship)
    fun applyReligion(religion: PhenomenonReligion)
}

interface MonsterSystem {
    fun applyMutation(mutation: AbsoluteMutation)
}

interface WorldNarrativeSystem {
    fun registerAlternateHistory(history: AlternateHistory)
    fun registerChronicle(chronicle: WorldChronicle)
}

interface ExpeditionSystem {
    fun startOtherSideExpedition(expedition: OtherSideExpedition)
}

interface ArtifactSystem {
    fun registerFullnessArtifact(artifact: FullnessArtifact)
}

interface AvatarSystem {
    fun setFullnessAvatar(avatar: FullnessAvatar)
}
