package com.grimreich.core

import kotlinx.serialization.Serializable

@Serializable
data class PersistentMetaDto(
    var totalSessionsFinished: Int,
    val unlockedLegacyBuffs: List<String>,
    var maxMetaAwarenessReached: Int
)

@Serializable
data class QuestProgressDto(
    val questId: String,
    val status: String,
    val currentStepIndex: Int,
    val variables: Map<String, Int>
)

@Serializable
data class QuestStateDto(
    val activeQuestIds: List<String>,
    val completedQuestIds: List<String>,
    val progress: Map<String, QuestProgressDto>
)

@Serializable
sealed interface PendingWorldActionDto {
    @Serializable
    data object None : PendingWorldActionDto
    @Serializable
    data class ResolveQuest(val questId: String) : PendingWorldActionDto
    @Serializable
    data class QuestCombatWin(val questId: String) : PendingWorldActionDto
    @Serializable
    data class Dialogue(
        val npcName: String,
        val npcRole: String,
        val nodeId: String,
        val relatedQuestId: String? = null
    ) : PendingWorldActionDto
}

@Serializable
data class SessionStateDto(
    val version: Int,
    var playerName: String? = null,
    var heroName: String? = null,
    var characterNameLocked: Boolean = false,
    var metaAwarenessLevel: Int = 0,

    var pendingAction: PendingWorldActionDto = PendingWorldActionDto.None,

    val party: List<HeroDto>,
    val hireableHeroes: List<HeroDto>,
    var activeHeroId: String? = null,
    val inventory: List<ItemDto>,
    val logEntries: List<String>,
    var gold: Int,

    val quest: QuestStateDto,
    val reputation: ReputationStateDto,
    val prayer: PrayerStateDto,
    val world: WorldStateDto,
    val combat: CombatStateDto,
    val knownNpcs: Map<String, List<NpcDto>>,
    val unlockedLoreIds: List<String>,
    val persistentMeta: PersistentMetaDto,
    var isExpeditionActive: Boolean,
    var lastSaveTimestamp: Long,
    
    var grimMutationPhase: Int = 0,
    val grantedRewardFlags: List<String> = emptyList(),
    val companionShadows: List<HeroDto> = emptyList(),
    val checksum: String? = null
)

@Serializable
data class SaveSnapshotDto(
    val version: Int,
    val timestamp: Long,
    val label: String,
    val session: SessionStateDto,
    val checksum: String? = null
)

@Serializable
data class HeroDto(
    val id: String,
    val name: String,
    val age: Int,
    val strength: Int,
    val agility: Int,
    val perception: Int,
    val intelligence: Int,
    val endurance: Int,
    val charisma: Int,
    val piety: Int,
    val virtue: Int,
    val divineFavor: Int,
    val sanity: Int,
    val corruption: Int,
    val morale: Int,
    val level: Int,
    val xp: Int,
    val attributePoints: Int,
    val portraitRes: String,
    val hp: Int,
    val maxHp: Int,
    val isDead: Boolean,
    val activeMutations: List<MutationDto>,
    val currentCareer: String?,
    val trait: String?,
    val skills: Map<String, Int>,
    val equipment: Map<String, String?>,
    val careerHistory: List<CareerEntryDto>,
    val abilities: List<AbilityDto>,
    val passiveAbilities: List<String> = emptyList()
)

@Serializable
data class CareerEntryDto(
    val careerName: String,
    val yearsServed: Int,
    val levelReached: Int,
    val dateReached: Long
)

@Serializable
data class AbilityDto(
    val id: String,
    val name: String,
    val type: String,
    val description: String? = null,
    val costValue: Int? = null
)

@Serializable
data class MutationDto(
    val id: String,
    val name: String,
    val description: String,
    val tier: String,
    val category: String = "PHYSICAL",
    val attributeModifiers: Map<String, Int>,
    val stabilityImpact: Int
)

@Serializable
data class ItemDto(
    val instanceId: String,
    val templateId: String,
    val name: String,
    val type: String,
    val slot: String?,
    val value: Int,
    val weight: Double,
    val rarity: String,
    val lore: String?,
    val effects: Map<String, Int>
)

@Serializable
data class ReputationStateDto(
    val cityFactions: Map<String, Map<String, Int>>,
    val globalFactions: Map<String, Int>
)

@Serializable
data class PrayerStateDto(
    val faith: Int,
    val virtue: Int,
    val sins: Int,
    val blessings: List<String>
)

@Serializable
data class WorldStateDto(
    val region: String,
    val locationId: String,
    val day: Int,
    val timeOfDay: String,
    val fatigue: Int,
    val lastEncounter: String,
    val season: String,
    val globalStability: Int,
    val weather: String,
    val echoIntensity: Float,
    val collapseProgress: Float,
    val collapseScenarioId: String? = null,
    val ontologicalLevel: Int,
    val discoveredLocations: List<String>,
    val cityEntryCount: Int,
    val verdictIncidentsSeen: Int,
    val reachedThresholds: List<Float> = emptyList()
)

@Serializable
data class CombatStateDto(
    val active: Boolean,
    val round: Int,
    val enemyName: String,
    val enemyHp: Int,
    val enemyMaxHp: Int,
    val enemyAttack: Int,
    val enemyDefense: Int,
    val enemyAgility: Int,
    val enemyIntelligence: Int,
    val enemyStrength: Int,
    val enemyEffects: List<StatusEffectDto>,
    val heroEffects: List<StatusEffectDto>,
    val log: List<String>,
    val currentTargetHeroId: String? = null,
    val activeHeroId: String? = null,
    val initiativeOrder: List<InitiativeSlotDto> = emptyList(),
    val currentTurnIndex: Int = 0
)

@Serializable
data class InitiativeSlotDto(
    val id: String,
    val isPlayer: Boolean,
    val initiativeValue: Int
)

@Serializable
data class NpcDto(
    val id: String,
    val name: String,
    val role: String,
    val factionId: String? = null,
    val personality: String = "Normal",
    val startNodeId: String? = null,
    val stability: Float = 1.0f
)

@Serializable
data class StatusEffectDto(
    val type: String,
    val duration: Int,
    val magnitude: Int
)
