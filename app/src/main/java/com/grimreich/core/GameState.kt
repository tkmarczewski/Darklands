package com.grimreich.core

import com.grimreich.grimreich.v1.NPC
import com.grimreich.grimreich.v1.Item
import kotlinx.serialization.Serializable

const val SAVE_VERSION = 12
const val INITIAL_GOLD = 100

data class GameState(
    var playerName: String? = null,
    var heroName: String? = null,
    var characterNameLocked: Boolean = false,
    var metaAwarenessLevel: Int = 0,
    var pendingAction: PendingWorldAction = PendingWorldAction.None,
    val party: MutableList<Hero> = mutableListOf(),
    val hireableHeroes: MutableList<Hero> = mutableListOf(),
    var activeHeroId: String? = null,
    val inventory: MutableList<Item> = mutableListOf(),
    val logEntries: MutableList<String> = mutableListOf(),
    var gold: Int = INITIAL_GOLD,
    var quest: QuestState = QuestState(),
    var reputation: ReputationState = ReputationState(),
    var prayer: PrayerState = PrayerState(),
    var world: WorldState = WorldState(),
    var combat: CombatState = CombatState(),
    val knownNpcs: MutableMap<String, List<NPC>> = mutableMapOf(),
    val unlockedLoreIds: MutableSet<String> = mutableSetOf(),
    var persistentMeta: PersistentMeta = PersistentMeta(),
    var isExpeditionActive: Boolean = false,
    var lastSaveTimestamp: Long = 0L,
    var grimMutationPhase: Int = 0,
    val grantedRewardFlags: MutableSet<String> = mutableSetOf(),
    val companionShadows: MutableList<Hero> = mutableListOf()
) {
    fun currentState(): GameState = this

    fun update(block: (GameState) -> Unit) {
        block(this)
    }

    fun deepCopy(): GameState {
        return this.copy(
            party = this.party.map { it.deepCopy() }.toMutableList(),
            hireableHeroes = this.hireableHeroes.map { it.deepCopy() }.toMutableList(),
            inventory = this.inventory.toMutableList(), // Item is data class
            logEntries = this.logEntries.toMutableList(),
            quest = this.quest.copy(
                activeQuestIds = this.quest.activeQuestIds.toMutableSet(),
                completedQuestIds = this.quest.completedQuestIds.toMutableSet(),
                failedQuestIds = this.quest.failedQuestIds.toMutableSet(),
                progress = this.quest.progress.mapValues { it.value.copy(variables = it.value.variables.toMutableMap()) }.toMutableMap(),
                worldFlags = this.quest.worldFlags.toMutableSet()
            ),
            reputation = this.reputation.copy(
                globalFactions = this.reputation.globalFactions.toMutableMap(),
                cityFactions = this.reputation.cityFactions.mapValues { it.value.toMutableMap() }.toMutableMap()
            ),
            prayer = this.prayer.copy(blessings = this.prayer.blessings.toMutableList()),
            world = this.world.copy(
                discoveredLocations = this.world.discoveredLocations.toMutableSet(),
                reachedThresholds = this.world.reachedThresholds.toMutableSet()
            ),
            combat = this.combat.copy(
                enemyEffects = this.combat.enemyEffects.map { it.copy() }.toMutableList(),
                heroEffects = this.combat.heroEffects.map { it.copy() }.toMutableList(),
                log = this.combat.log.toMutableList(),
                initiativeOrder = this.combat.initiativeOrder.toMutableList()
            ),
            knownNpcs = this.knownNpcs.mapValues { entry -> entry.value.map { it.deepCopy() } }.toMutableMap(),
            unlockedLoreIds = this.unlockedLoreIds.toMutableSet(),
            persistentMeta = this.persistentMeta.copy(
                unlockedLegacyBuffs = this.persistentMeta.unlockedLegacyBuffs.toMutableSet(),
                unitedSelves = this.persistentMeta.unitedSelves.toMutableList()
            ),
            grantedRewardFlags = this.grantedRewardFlags.toMutableSet(),
            companionShadows = this.companionShadows.map { it.deepCopy() }.toMutableList()
        )
    }

    fun trimLogs() { if (logEntries.size > 100) { val last = logEntries.takeLast(100); logEntries.clear(); logEntries.addAll(last) } }
    
    fun normalizeState() {
        party.forEach { it.normalize() }
        hireableHeroes.forEach { it.normalize() }
        companionShadows.forEach { it.normalize() }
        trimLogs()
        prayer.normalize()
        
        world.globalStability = world.globalStability.coerceIn(0, 100)
        world.echoIntensity = world.echoIntensity.coerceIn(0f, 5f)

        // Sync anchor identity for persistence
        if (playerName != null) {
            persistentMeta.anchorIdentity = playerName
        }
    }
}

@Serializable
enum class QuestStatus {
    locked, available, active, objective_met, completed, failed;

    companion object {
        @JvmField val LOCKED = locked
        @JvmField val AVAILABLE = available
        @JvmField val ACTIVE = active
        @JvmField val OBJECTIVE_MET = objective_met
        @JvmField val COMPLETED = completed
        @JvmField val FAILED = failed
    }
}

@Serializable
enum class QuestCategory { 
    combat, social, investigation, mixed, meta, anomaly, drama, beast, intrigue, expedition, dialogue, ritual, bounty;

    companion object {
        @JvmField val COMBAT = combat
        @JvmField val SOCIAL = social
        @JvmField val INVESTIGATION = investigation
        @JvmField val MIXED = mixed
        @JvmField val META = meta
        @JvmField val ANOMALY = anomaly
        @JvmField val DRAMA = drama
        @JvmField val BEAST = beast
        @JvmField val INTRIGUE = intrigue
        @JvmField val EXPEDITION = expedition
        @JvmField val DIALOGUE = dialogue
        @JvmField val RITUAL = ritual
        @JvmField val BOUNTY = bounty
    }
}

@Serializable
enum class StepType { 
    kill, collect, talk, reach, combat, dialogue, investigation, social, meta, expedition;

    companion object {
        @JvmField val KILL = kill
        @JvmField val COLLECT = collect
        @JvmField val TALK = talk
        @JvmField val REACH = reach
        @JvmField val COMBAT = combat
        @JvmField val DIALOGUE = dialogue
        @JvmField val INVESTIGATION = investigation
        @JvmField val SOCIAL = social
        @JvmField val META = meta
        @JvmField val EXPEDITION = expedition
    }
}

@Serializable
sealed class PendingWorldAction {
    @Serializable
    object None : PendingWorldAction()
    @Serializable
    data class ResolveQuest(val questId: String) : PendingWorldAction()
    @Serializable
    data class QuestCombatWin(val questId: String) : PendingWorldAction()
    @Serializable
    data class Dialogue(
        val npcName: String,
        val npcRole: String,
        val nodeId: String,
        val relatedQuestId: String? = null
    ) : PendingWorldAction()
}

@Serializable
data class QuestState(
    val activeQuestIds: MutableSet<String> = mutableSetOf(),
    val completedQuestIds: MutableSet<String> = mutableSetOf(),
    val failedQuestIds: MutableSet<String> = mutableSetOf(),
    val progress: MutableMap<String, QuestProgress> = mutableMapOf(),
    val worldFlags: MutableSet<String> = mutableSetOf()
)

@Serializable
data class QuestProgress(
    val questId: String,
    var status: QuestStatus = QuestStatus.locked,
    var currentStepIndex: Int = 0,
    val variables: MutableMap<String, String> = mutableMapOf()
)

@Serializable
data class ReputationState(
    val globalFactions: MutableMap<String, Int> = mutableMapOf(),
    val cityFactions: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()
)

@Serializable
data class PrayerState(
    var faith: Int = 0,
    var virtue: Int = 50,
    var sins: Int = 0,
    val blessings: MutableList<String> = mutableListOf()
) {
    fun normalize() {
        faith = faith.coerceIn(0, 100)
        virtue = virtue.coerceIn(0, 100)
        sins = sins.coerceAtLeast(0)
    }
}

@Serializable
data class WorldState(
    var region: String = "wybrzeze_polnocne",
    var locationId: String = "wybrzeze_polnocne",
    var day: Int = 1,
    var timeOfDay: String = "morning",
    var fatigue: Int = 0,
    var lastEncounter: Long = 0L,
    var season: Season = Season.spring,
    var globalStability: Int = 100,
    var weather: WeatherType = WeatherType.clear,
    var echoIntensity: Float = 0f,
    var collapseProgress: Float = 0f,
    var collapseScenarioId: String? = null,
    var ontologicalLevel: com.grimreich.grimreich.v1.OntologicalLevel = com.grimreich.grimreich.v1.OntologicalLevel.material,
    var cityEntryCount: Int = 0,
    var verdictIncidentsSeen: Int = 0,
    val discoveredLocations: MutableSet<String> = mutableSetOf(),
    val reachedThresholds: MutableSet<String> = mutableSetOf()
)

@Serializable
data class CombatState(
    var active: Boolean = false,
    var round: Int = 0,
    var enemyName: String = "",
    var enemyType: String? = null,
    var enemyHp: Int = 0,
    var enemyMaxHp: Int = 0,
    var enemyAttack: Int = 0,
    var enemyDefense: Int = 0,
    var enemyAgility: Int = 0,
    var enemyIntelligence: Int = 0,
    var enemyStrength: Int = 0,
    var enemyStamina: Int = 0,
    var enemyMorale: Int = 80,
    val enemyEffects: MutableList<StatusEffect> = mutableListOf(),
    val heroEffects: MutableList<StatusEffect> = mutableListOf(),
    val log: MutableList<String> = mutableListOf(),
    var currentTargetHeroId: String? = null,
    var activeHeroId: String? = null,
    val initiativeOrder: MutableList<InitiativeSlot> = mutableListOf(),
    var currentTurnIndex: Int = 0
)

@Serializable
data class InitiativeSlot(
    val id: String,
    val isPlayer: Boolean,
    val initiativeValue: Int
)

@Serializable
data class SessionStateDto(
    val version: Int,
    val playerName: String? = null,
    val heroName: String? = null,
    val characterNameLocked: Boolean = false,
    val metaAwarenessLevel: Int = 0,
    val pendingAction: PendingWorldActionDto,
    val party: List<HeroDto>,
    val hireableHeroes: List<HeroDto>,
    val activeHeroId: String? = null,
    val inventory: List<ItemDto>,
    val logEntries: List<String>,
    val gold: Int,
    val quest: QuestStateDto,
    val reputation: ReputationStateDto,
    val prayer: PrayerStateDto,
    val world: WorldStateDto,
    val combat: CombatStateDto,
    val knownNpcs: Map<String, List<NpcDto>>,
    val unlockedLoreIds: List<String>,
    val persistentMeta: PersistentMetaDto,
    val isExpeditionActive: Boolean = false,
    val lastSaveTimestamp: Long = 0,
    val grimMutationPhase: Int = 0,
    val grantedRewardFlags: List<String> = emptyList(),
    val companionShadows: List<HeroDto> = emptyList(),
    val checksum: String? = null
)

@Serializable
sealed class PendingWorldActionDto {
    @Serializable
    object None : PendingWorldActionDto()
    @Serializable
    data class ResolveQuest(val questId: String) : PendingWorldActionDto()
    @Serializable
    data class QuestCombatWin(val questId: String) : PendingWorldActionDto()
    @Serializable
    data class Dialogue(
        val npcName: String,
        val npcRole: String,
        val nodeId: String,
        val relatedQuestId: String? = null
    ) : PendingWorldActionDto()
}

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
    val passiveAbilities: List<String>,
    val subjectType: String,
    val ontologicalMass: Int,
    val traumaMarks: List<TraumaDto>,
    val ontologicalStability: Float,
    val activeStatusEffects: List<StatusEffectDto>
)

@Serializable
data class CareerEntryDto(
    val careerName: String,
    val daysServed: Int,
    val levelReached: Int,
    val dateReached: Long
)

@Serializable
data class AbilityDto(
    val id: String,
    val name: String,
    val type: String,
    val description: String?,
    val costValue: Int?
)

@Serializable
data class MutationDto(
    val id: String,
    val name: String,
    val description: String,
    val tier: String,
    val category: String,
    val attributeModifiers: Map<String, Int>,
    val stabilityImpact: Int
)

@Serializable
data class QuestStateDto(
    val activeQuestIds: List<String>,
    val completedQuestIds: List<String>,
    val failedQuestIds: List<String>,
    val progress: Map<String, QuestProgressDto>
)

@Serializable
data class QuestProgressDto(
    val questId: String,
    val status: String,
    val currentStepIndex: Int,
    val variables: Map<String, String>
)

@Serializable
data class ReputationStateDto(
    val globalFactions: Map<String, Int>,
    val cityFactions: Map<String, Map<String, Int>>
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
    val lastEncounter: Long,
    val season: String,
    val globalStability: Int,
    val weather: String,
    val echoIntensity: Float,
    val collapseProgress: Float,
    val collapseScenarioId: String?,
    val ontologicalLevel: Int,
    val discoveredLocations: Set<String>,
    val cityEntryCount: Int,
    val verdictIncidentsSeen: Int,
    val reachedThresholds: List<String>
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
    val enemyStamina: Int,
    val enemyMorale: Int,
    val enemyEffects: List<StatusEffectDto>,
    val heroEffects: List<StatusEffectDto>,
    val log: List<String>,
    val currentTargetHeroId: String?,
    val activeHeroId: String?,
    val initiativeOrder: List<InitiativeSlotDto>,
    val currentTurnIndex: Int
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
    val factionId: String?,
    val personality: String,
    val startNodeId: String?,
    val stability: Float
)

@Serializable
data class StatusEffectDto(
    val type: String,
    val duration: Int,
    val magnitude: Int
)

@Serializable
data class PersistentMetaDto(
    val anchorIdentity: String?,
    val totalSessionsFinished: Int,
    val unlockedLegacyBuffs: List<String>,
    val maxMetaAwarenessReached: Int,
    val unitedSelves: List<String>
)

@Serializable
data class ItemDto(
    val instanceId: String,
    val templateId: String,
    val name: String,
    val type: String,
    val slot: String? = null,
    val value: Int = 0,
    val weight: Double = 0.0,
    val rarity: String = "common",
    val lore: String? = null,
    val effects: Map<String, Int> = emptyMap()
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
data class TraumaDto(
    val id: String,
    val name: String,
    val description: String,
    val statModifiers: Map<String, Int>,
    val severity: Int
)
