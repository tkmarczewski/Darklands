package com.grimreich.core

import kotlinx.serialization.Serializable

@Serializable
data class SessionStateDto(
    val version: Int = 1,

    val playerName: String? = null,
    val characterNameLocked: Boolean = false,
    val metaAwarenessLevel: Int = 0,

    val grimCurrentRegion: String = "wybrzeze_polnocne",
    val grimPendingExpeditionName: String? = null,
    val pendingQuestId: String? = null,

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
    val lastSaveTimestamp: Long = System.currentTimeMillis()
)

@Serializable
data class HeroDto(
    val id: String,
    val name: String,
    val age: Int,
    val strength: Int = 10,
    val agility: Int = 10,
    val perception: Int = 10,
    val intelligence: Int = 10,
    val endurance: Int = 10,
    val charisma: Int = 10,
    val piety: Int = 10,
    val virtue: Int = 0,
    val divineFavor: Int = 50,
    val sanity: Int = 100,
    val corruption: Int = 0,
    val morale: Int = 70,
    val level: Int = 1,
    val xp: Int = 0,
    val attributePoints: Int = 0,
    val portraitRes: String = "port_knight",
    val hp: Int = 30,
    val maxHp: Int = 30,
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
    val slot: String? = null,
    val value: Int = 0,
    val weight: Double = 0.0,
    val rarity: String = "common",
    val effects: Map<String, Int> = emptyMap()
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
    val cityFactions: Map<String, Map<String, Int>> = emptyMap()
)

@Serializable
data class PrayerStateDto(
    val faith: Int = 10,
    val virtue: Int = 50,
    val sins: Int = 0,
    val blessings: List<String> = emptyList()
)

@Serializable
data class WorldStateDto(
    val region: String = "wybrzeze_polnocne",
    val location: String = "wybrzeze_polnocne",
    val day: Int = 1,
    val timeOfDay: String = "morning",
    val fatigue: Int = 0,
    val lastEncounter: String = "none",
    val season: String = "SPRING",
    val globalStability: Int = 100,
    val weather: String = "CLEAR",
    val echoIntensity: Float = 0.0f,
    val collapseProgress: Float = 0.0f,
    val ontologicalLevel: Int = 0,
    val discoveredLocations: List<String> = emptyList(),
    val cityEntryCount: Int = 0
)

@Serializable
data class CombatStateDto(
    val active: Boolean = false,
    val round: Int = 0,
    val enemyName: String = "",
    val enemyHp: Int = 0,
    val enemyMaxHp: Int = 0,
    val enemyAttack: Int = 5,
    val enemyDefense: Int = 3,
    val enemyAgility: Int = 5,
    val enemyIntelligence: Int = 5,
    val enemyStrength: Int = 5,
    val enemyEffects: List<StatusEffectDto> = emptyList(),
    val heroEffects: List<StatusEffectDto> = emptyList(),
    val log: List<String> = emptyList()
)

@Serializable
data class StatusEffectDto(
    val type: String,
    val duration: Int,
    val strength: Int
)
