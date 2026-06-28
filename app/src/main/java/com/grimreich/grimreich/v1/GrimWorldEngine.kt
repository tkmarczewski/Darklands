package com.grimreich.grimreich.v1

class GrimWorldEngine(
    val regionSystem: RegionSystem,
    private val npcSystem: NPCSystem,
    private val monsterSystem: MonsterSystem,
    private val narrativeSystem: WorldNarrativeSystem,
    private val expeditionSystem: ExpeditionSystem,
    private val artifactSystem: ArtifactSystem,
    private val avatarSystem: AvatarSystem
) {
    var echoIntensity: Float = 0f
    var mutationPhase: Int = 0

    val query: GrimWorldQuery = GrimWorldQuery(
        regionSystem,
        npcSystem,
        artifactSystem,
        avatarSystem,
        expeditionSystem,
        narrativeSystem
    )

    fun loadRegion(region: RegionConsciousness, time: NonlinearTime, arch: FullnessArchitecture) {
        regionSystem.applyConsciousness(region)
        regionSystem.applyNonlinearTime(time)
        regionSystem.applyArchitecture(arch)
    }

    fun updateWorldCollapse(collapse: WorldCollapse) = regionSystem.applyCollapse(collapse)

    fun registerNPC(path: NPCLifePath, rels: List<TriLayerRelationship>) {
        npcSystem.applyLifePath(path)
        rels.forEach { npcSystem.applyRelationship(it) }
    }

    fun registerReligion(religion: PhenomenonReligion) = npcSystem.applyReligion(religion)
    fun registerArtifact(artifact: FullnessArtifact) = artifactSystem.registerFullnessArtifact(artifact)
    fun setAvatar(avatar: FullnessAvatar) = avatarSystem.setFullnessAvatar(avatar)
    fun startExpedition(expedition: OtherSideExpedition) = expeditionSystem.startOtherSideExpedition(expedition)
    fun recordHistory(history: AlternateHistory, chronicle: WorldChronicle) {
        narrativeSystem.registerAlternateHistory(history)
        narrativeSystem.registerChronicle(chronicle)
    }
    fun applyAbsoluteMutation(mutation: AbsoluteMutation) = monsterSystem.applyMutation(mutation)

    fun queryNpc(name: String): OtherSideNpcState? {
        val sys = npcSystem as? DefaultNPCSystem ?: return null
        val npc = sys.getNPCPath(name) ?: return null
        val rels = sys.getRelationships(name)
        val religion = sys.getReligionByNpc(name)
        return OtherSideNpcLogic().evaluate(npc, rels, religion)
    }
}
