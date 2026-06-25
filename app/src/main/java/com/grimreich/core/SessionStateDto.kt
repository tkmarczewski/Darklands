package com.grimreich.core

import kotlinx.serialization.Serializable
import com.grimreich.core.mutations.MutationDto

@Serializable
data class SessionStateDto(
    val version: Int = 1,

    val playerName: String? = null,
    val heroName: String? = null, // Hero name field
    val characterNameLocked: Boolean = false,
    val metaAwarenessLevel: Int = 0,

    val grimCurrentRegion: String = "wybrzeze_polnocne",
    val grimPendingExpeditionName: String? = null,
    val pendingQuestId: String? = null,
    val pendingDialogueNpcName: String? = null,
    val pendingDialogueNpcRole: String? = null,
    val pendingDialogueNodeId: String? = null,

    val party: List<HeroDto> = emptyList(),
    val hireableHeroes: List<HeroDto> = emptyList(),
    val activeHeroId: String? = null,
    val inventory: List<ItemDto> = emptyList(),
    val logEntries: List<String> = emptyList(),
    val gold: Int = 0,

    val quest: QuestStateDto = QuestStateDto(),
    val reputation: ReputationStateDto = ReputationStateDto(),
    val prayer: PrayerStateDto = PrayerStateDto(),
    val world: WorldStateDto = WorldStateDto(),
    val combat: CombatStateDto = CombatStateDto(),
    val knownNpcs: Map<String, List<NpcDto>> = emptyMap(),
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
    val activeMutations: List<MutationDto> = emptyList(),
    val currentCareer: String? = null,
    val trait: String? = null,
    val skills: Map<String, Int> = emptyMap(),
    val equipment: Map<String, String?> = emptyMap()
)

@Serializable
data class ItemDto(
    val id: String,
    val name: String,
    val type: String,
    val slot: String?,
    val value: Int,
    val weight: Double,
    val rarity: String,
    val lore: String? = null,
    val effects: Map<String, Int>
)

@Serializable
data class QuestStateDto(
    val activeQuests: List<String> = emptyList(),
    val completedQuests: List<String> = emptyList(),
    val questProgress: Map<String, Int> = emptyMap(),
    val activeEndgameQuests: List<String> = emptyList(),
    val completedEndgameQuests: List<String> = emptyList()
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
    val region: String = "",
    val location: String = "",
    val day: Int = 1,
    val timeOfDay: String = "",
    val fatigue: Int = 0,
    val lastEncounter: String = "",
    val season: String = "",
    val globalStability: Int = 100,
    val weather: String = "",
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
    val round: Int = 0,
    val enemyName: String = "",
    val enemyHp: Int = 0,
    val enemyMaxHp: Int = 0,
    val enemyAttack: Int = 0,
    val enemyDefense: Int = 0,
    val enemyAgility: Int = 0,
    val enemyIntelligence: Int = 0,
    val enemyStrength: Int = 0,
    val enemyEffects: List<StatusEffectDto> = emptyList(),
    val heroEffects: List<StatusEffectDto> = emptyList(),
    val log: List<String> = emptyList()
)

@Serializable
data class NpcDto(
    val id: String,
    val name: String,
    val role: String,
    val factionId: String? = null,
    val startNodeId: String? = null,
    val stability: Float = 1.0f
)

@Serializable
data class StatusEffectDto(
    val type: String,
    val duration: Int,
    val magnitude: Int
)
