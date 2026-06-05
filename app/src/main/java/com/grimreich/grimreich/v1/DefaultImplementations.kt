package com.grimreich.grimreich.v1

class DefaultMonsterSystem : MonsterSystem {
    private val mutations = mutableMapOf<String, AbsoluteMutation>()
    override fun applyMutation(mutation: AbsoluteMutation) { mutations[mutation.targetName] = mutation }
    fun getMutation(name: String): AbsoluteMutation? = mutations[name]
}

class DefaultNarrativeSystem : WorldNarrativeSystem {
    private val histories = mutableMapOf<String, AlternateHistory>()
    private val chronicles = mutableMapOf<String, WorldChronicle>()
    override fun registerAlternateHistory(history: AlternateHistory) { histories[history.historyName] = history }
    override fun registerChronicle(chronicle: WorldChronicle) { chronicles[chronicle.chronicleName] = chronicle }
    fun allChronicles(): List<WorldChronicle> = chronicles.values.toList()
}

class DefaultExpeditionSystem : ExpeditionSystem {
    private val expeditions = mutableListOf<OtherSideExpedition>()
    override fun startOtherSideExpedition(expedition: OtherSideExpedition) { expeditions.add(expedition) }
    fun activeExpeditions(): List<OtherSideExpedition> = expeditions.toList()
}

class DefaultArtifactSystem : ArtifactSystem {
    private val artifacts = mutableMapOf<String, FullnessArtifact>()
    override fun registerFullnessArtifact(artifact: FullnessArtifact) { artifacts[artifact.artifactName] = artifact }
    fun allArtifacts(): List<FullnessArtifact> = artifacts.values.toList()
}

class DefaultAvatarSystem : AvatarSystem {
    var currentAvatar: FullnessAvatar? = null
        private set
    override fun setFullnessAvatar(avatar: FullnessAvatar) { currentAvatar = avatar }
}

object GrimWorldEngineFactory {
    fun create(): GrimWorldEngine {
        val region = DefaultRegionSystem()
        val npc = DefaultNPCSystem()
        val monster = DefaultMonsterSystem()
        val narrative = DefaultNarrativeSystem()
        val expedition = DefaultExpeditionSystem()
        val artifact = DefaultArtifactSystem()
        val avatar = DefaultAvatarSystem()
        return GrimWorldEngine(region, npc, monster, narrative, expedition, artifact, avatar)
    }
}
