package com.darklandsmobile.grimreich.v1

data class NonlinearTime(
    var regionName: String,
    var mistTimeLevel: Int,
    var bloodTimeLevel: Int,
    var reflectionTimeLevel: Int,
    var fullnessTimeLevel: Int,
    var chaosTimeLevel: Int,
    var activeTimeEffects: List<String>,
    var regionImpact: String,
    var npcImpact: String,
    var monsterImpact: String,
    var endingImpact: String
)

data class TriLayerRelationship(
    var npcName: String,
    var mistRelation: String,
    var bloodRelation: String,
    var reflectionRelation: String,
    var fullnessRelationLevel: Int,
    var chaosRelationLevel: Int,
    var emotionalImpact: String,
    var regionImpact: String,
    var endingImpact: String
)

data class PhenomenonReligion(
    var religionName: String,
    var phenomenon: String,
    var dogma: String,
    var rituals: List<String>,
    var prophets: List<String>,
    var artifacts: List<String>,
    var regionImpact: String,
    var npcImpact: String,
    var endingImpact: String
)

data class FullnessArtifact(
    var artifactName: String,
    var mistEffect: String,
    var bloodEffect: String,
    var reflectionEffect: String,
    var fullnessEffect: String,
    var chaosEffect: String,
    var regionImpact: String,
    var npcImpact: String,
    var endingImpact: String
)

data class OtherSideExpedition(
    var expeditionName: String,
    var logicalLayer: String,
    var symmetricLayer: String,
    var zeroLayer: String,
    var enemies: List<String>,
    var rewards: List<String>,
    var regionImpact: String,
    var endingImpact: String,
    var difficultyTier: Int = 1
)

data class AlternateHistory(
    var historyName: String,
    var mistVersion: String,
    var bloodVersion: String,
    var reflectionVersion: String,
    var fullnessImpact: String,
    var chaosImpact: String,
    var regionImpact: String,
    var npcImpact: String,
    var endingImpact: String
)

data class FullnessAvatar(
    var avatarName: String,
    var mistForm: String,
    var bloodForm: String,
    var reflectionForm: String,
    var fullnessLevel: Int,
    var chaosLevel: Int,
    var abilities: List<String>,
    var regionImpact: String,
    var npcImpact: String,
    var endingImpact: String
)

data class AbsoluteMutation(
    var targetName: String,
    var zeroMindLevel: Int,
    var zeroBodyLevel: Int,
    var zeroSoulLevel: Int,
    var worldErasureLevel: Int,
    var activeEffects: List<String>,
    var regionImpact: String,
    var npcImpact: String,
    var monsterImpact: String,
    var endingImpact: String
)

data class WorldChronicle(
    var chronicleName: String,
    var mistRecord: String,
    var bloodRecord: String,
    var reflectionRecord: String,
    var fullnessRecord: String,
    var chaosRecord: String,
    var regionImpact: String,
    var npcImpact: String,
    var endingImpact: String
)

data class RegionConsciousness(
    var regionName: String,
    var mistMind: String,
    var bloodBody: String,
    var reflectionSoul: String,
    var emotionalState: String,
    var memory: List<String>,
    var reactions: List<String>,
    var regionImpact: String,
    var endingImpact: String
)

data class FullnessArchitecture(
    var structureName: String,
    var mistForm: String,
    var bloodForm: String,
    var reflectionForm: String,
    var fullnessEffect: String,
    var chaosEffect: String,
    var regionImpact: String,
    var endingImpact: String
)

data class NPCLifePath(
    var npcName: String,
    var mistFate: String,
    var bloodFate: String,
    var reflectionFate: String,
    var fullnessLevel: Int,
    var chaosLevel: Int,
    var timelineEvents: List<String>,
    var npcImpact: String,
    var endingImpact: String
)

data class WorldCollapse(
    var collapseStage: String,
    var phenomenonLoss: Int,
    var layerCollapse: Int,
    var regionDecay: Int,
    var npcDecay: Int,
    var monsterDecay: Int,
    var historyLoss: Int,
    var endingImpact: String
)
