package com.grimreich.core

import kotlinx.serialization.Serializable

@Serializable
data class PersistentMetaDto(
    val totalSessionsFinished: Int = 0,
    val unlockedLegacyBuffs: List<String> = emptyList(),
    val maxMetaAwarenessReached: Int = 0
)

@Serializable
data class QuestProgressDto(
    val questId: String,
    val status: String,
    val currentStepIndex: Int = 0,
    val variables: Map<String, Int> = emptyMap()
)

@Serializable
data class QuestStateDto(
    val activeQuestIds: List<String> = emptyList(),
    val completedQuestIds: List<String> = emptyList(),
    val progress: Map<String, QuestProgressDto> = emptyMap()
)

@Serializable
data class SessionStateDto(
    val version: Int = 3,
    val playerName: String? = null,
    val heroName: String? = null,
    val characterNameLocked: Boolean = false,
    val metaAwarenessLevel: Int = 0,
    val grimCurrentRegion: String = "wybrzeze_polnocne",
    val pendingQuestId: String? = null,
    val pendingDialogueNpcName: String? = null,
    val pendingDialogueNpcRole: String? = null,
    val pendingDialogueNodeId: String? = null,
    val party: List<HeroDto> = emptyList(),
    val hireableHeroes: List<HeroDto> = emptyList(),
    val activeHeroId: String? = null,
    val inventory: List<ItemDto> = emptyList(),
    val logEntries: List<String> = emptyList(),
    val gold: Int = 100,
    val quest: QuestStateDto = QuestStateDto(),
    val reputation: ReputationStateDto = ReputationStateDto(),
    val prayer: PrayerStateDto = PrayerStateDto(),
    val world: WorldStateDto = WorldStateDto(),
    val combat: CombatStateDto = CombatStateDto(),
    val knownNpcs: Map<String, List<NpcDto>> = emptyMap(),
    val unlockedLoreIds: List<String> = emptyList(),
    val persistentMeta: PersistentMetaDto = PersistentMetaDto(),
    val isExpeditionActive: Boolean = false,
    val lastSaveTimestamp: Long = 0
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
    val isDead: Boolean = false,
    val activeMutations: List<MutationDto> = emptyList(),
    val currentCareer: String? = null,
    val trait: String? = null,
    val skills: Map<String, Int> = emptyMap(),
    val equipment: Map<String, String?> = emptyMap(),
    val careerHistory: List<CareerEntryDto> = emptyList(),
    val abilities: List<AbilityDto> = emptyList()
)

@Serializable
data class CareerEntryDto(
    val careerName: String,
    val levelReached: Int,
    val dateReached: Long
)

@Serializable
data class AbilityDto(
    val id: String,
    val name: String,
    val type: String
)

@Serializable
data class MutationDto(
    val id: String,
    val name: String,
    val tier: String,
    val attributeModifiers: Map<String, Int>,
    val stabilityImpact: Int
)

@Serializable
data class ItemDto(
    val id: String,
    val name: String,
    val type: String,
    val slot: String? = null,
    val value: Int = 0,
    val weight: Double = 0.0,
    val rarity: String = "COMMON",
    val lore: String? = null,
    val effects: Map<String, Int> = emptyMap()
)

@Serializable
data class ReputationStateDto(
    val cityFactions: Map<String, Map<String, Int>> = emptyMap(),
    val globalFactions: Map<String, Int> = emptyMap()
)

@Serializable
data class PrayerStateDto(
    val faith: Int = 0,
    val virtue: Int = 0,
    val sins: Int = 0,
    val blessings: List<String> = emptyList()
)

@Serializable
data class WorldStateDto(
    val region: String = "Pogranicze",
    val location: String = "wybrzeze_polnocne",
    val day: Int = 1,
    val timeOfDay: String = "morning",
    val fatigue: Int = 0,
    val lastEncounter: String = "",
    val season: String = "AUTUMN",
    val globalStability: Int = 100,
    val weather: String = "CLEAR",
    val echoIntensity: Float = 0f,
    val collapseProgress: Float = 0f,
    val ontologicalLevel: Int = 0,
    val discoveredLocations: List<String> = emptyList(),
    val cityEntryCount: Int = 0,
    val verdictIncidentsSeen: Int = 0
)

@Serializable
data class CombatStateDto(
    val active: Boolean = false,
    val round: Int = 1,
    val enemyName: String = "",
    val enemyHp: Int = 0,
    val enemyMaxHp: Int = 0,
    val enemyAttack: Int = 0,
    val enemyDefense: Int = 0,
    val enemyAgility: Int = 10,
    val enemyIntelligence: Int = 10,
    val enemyStrength: Int = 10,
    val enemyEffects: List<StatusEffectDto> = emptyList(),
    val heroEffects: List<StatusEffectDto> = emptyList(),
    val log: List<String> = emptyList()
)

@Serializable
data class StatusEffectDto(
    val type: String,
    val duration: Int,
    val magnitude: Int
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
