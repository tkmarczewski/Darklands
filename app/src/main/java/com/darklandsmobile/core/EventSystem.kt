package com.darklandsmobile.core

// ─── Konteksty i kategorie eventów ───────────────────────────────────────────

enum class EventContext {
    CITY,
    WILDERNESS,
    DUNGEON,
    SPECIAL_LOCATION
}

enum class EventCategory {
    RANDOM_ENCOUNTER,
    CITY_SERVICE,
    QUEST,
    STORY,
    SYSTEMIC
}

// ─── Identyfikatory ──────────────────────────────────────────────────────────

@JvmInline
value class EventId(val value: String)

@JvmInline
value class EventNodeId(val value: String)

@JvmInline
value class EventOptionId(val value: String)

// ─── Główne modele ────────────────────────────────────────────────────────────

data class Event(
    val id: EventId,
    val context: EventContext,
    val category: EventCategory,
    val weight: Int = 1,
    val rootNodeId: EventNodeId,
    val conditions: List<EventCondition> = emptyList(),
    val tags: Set<String> = emptySet()
)

data class EventNode(
    val id: EventNodeId,
    val eventId: EventId,
    val textKey: String,
    val illustrationAsset: String?,
    val options: List<EventOption>
)

data class EventOption(
    val id: EventOptionId,
    val textKey: String,
    val requirements: List<EventRequirement> = emptyList(),
    val outcome: EventOutcome
)

// ─── Warunki losowania eventów ────────────────────────────────────────────────

sealed interface EventCondition {
    fun isSatisfied(gameState: GameState, worldState: WorldState): Boolean
}

data class TimeOfDayCondition(
    val allowed: Set<TimeOfDay>
) : EventCondition {
    override fun isSatisfied(gameState: GameState, worldState: WorldState): Boolean =
        allowed.contains(worldState.timeOfDay)
}

data class SeasonCondition(
    val allowed: Set<Season>
) : EventCondition {
    override fun isSatisfied(gameState: GameState, worldState: WorldState): Boolean =
        allowed.contains(worldState.season)
}

data class LocationTagCondition(
    val requiredTags: Set<String>
) : EventCondition {
    override fun isSatisfied(gameState: GameState, worldState: WorldState): Boolean =
        requiredTags.all { worldState.currentLocationTags.contains(it) }
}

data class ReputationCondition(
    val faction: Faction,
    val min: Int? = null,
    val max: Int? = null
) : EventCondition {
    override fun isSatisfied(gameState: GameState, worldState: WorldState): Boolean {
        val rep = gameState.reputationState.getReputation(faction)
        if (min != null && rep < min) return false
        if (max != null && rep > max) return false
        return true
    }
}

data class QuestStateCondition(
    val questId: String,
    val requiredStage: String
) : EventCondition {
    override fun isSatisfied(gameState: GameState, worldState: WorldState): Boolean =
        gameState.questState.getStage(questId) == requiredStage
}

// ─── Wymagania na opcje ───────────────────────────────────────────────────────

enum class RequirementTarget {
    AnyHero,
    PartyLeader,
    SpecificHeroIndex
}

sealed interface EventRequirement {
    fun isMet(party: Party, gameState: GameState): Boolean
}

data class SkillRequirement(
    val skill: HeroSkill,
    val minValue: Int,
    val target: RequirementTarget = RequirementTarget.AnyHero,
    val heroIndex: Int? = null
) : EventRequirement {
    override fun isMet(party: Party, gameState: GameState): Boolean = when (target) {
        RequirementTarget.AnyHero ->
            party.members.any { it.skills.getLevel(skill) >= minValue }
        RequirementTarget.PartyLeader ->
            party.members.firstOrNull()?.skills?.getLevel(skill) ?: 0 >= minValue
        RequirementTarget.SpecificHeroIndex ->
            heroIndex != null &&
                heroIndex in party.members.indices &&
                party.members[heroIndex].skills.getLevel(skill) >= minValue
    }
}

data class VirtueRequirement(
    val virtue: Virtue,
    val minValue: Int
) : EventRequirement {
    override fun isMet(party: Party, gameState: GameState): Boolean {
        val avg = party.members.map { it.virtues.getLevel(virtue) }.average()
        return avg >= minValue
    }
}

data class PartySizeRequirement(
    val minMembers: Int? = null,
    val maxMembers: Int? = null
) : EventRequirement {
    override fun isMet(party: Party, gameState: GameState): Boolean {
        val size = party.members.size
        if (minMembers != null && size < minMembers) return false
        if (maxMembers != null && size > maxMembers) return false
        return true
    }
}

data class AttributeRequirement(
    val minAverageAttributes: Map<HeroAttribute, Int> = emptyMap()
) : EventRequirement {
    override fun isMet(party: Party, gameState: GameState): Boolean =
        minAverageAttributes.all { (attr, min) ->
            party.members.map { it.attributes.get(attr) }.average() >= min
        }
}

// ─── Skutki (outcomes) ────────────────────────────────────────────────────────

sealed interface EventOutcome

data class EventGotoNode(val nodeId: EventNodeId) : EventOutcome

data class EventEnd(val result: EventEndResult = EventEndResult.Neutral) : EventOutcome

enum class EventEndResult { Neutral, Success, Failure, Escape, Death }

data class ModifyReputationOutcome(
    val factionChanges: Map<Faction, Int>
) : EventOutcome

data class ModifyVirtueOutcome(
    val virtueChanges: Map<Virtue, Int>
) : EventOutcome

data class ModifyResourcesOutcome(
    val goldDelta: Int = 0,
    val itemChanges: List<ItemChange> = emptyList()
) : EventOutcome

data class ItemChange(
    val item: Item,
    val delta: Int,
    val target: InventoryTarget = InventoryTarget.PartyStash
)

enum class InventoryTarget { PartyStash, SpecificHero, RandomHero }

data class ModifyHealthOutcome(
    val hpDeltaPerHero: Int,
    val canKill: Boolean = false
) : EventOutcome

data class ModifySkillOutcome(
    val skill: HeroSkill,
    val delta: Int,
    val target: RequirementTarget = RequirementTarget.AnyHero
) : EventOutcome

data class ModifyTimeOutcome(val hours: Int) : EventOutcome

data class ModifyWorldStateOutcome(
    val locationId: String? = null,
    val flagsToSet: Set<String> = emptySet()
) : EventOutcome

data class StartCombatOutcome(
    val encounterId: String,
    val surpriseParty: Boolean = false,
    val surpriseEnemies: Boolean = false
) : EventOutcome

data class StartQuestOutcome(val questId: String) : EventOutcome

data class AdvanceQuestOutcome(
    val questId: String,
    val nextStage: String
) : EventOutcome

data class ChainOutcome(val outcomes: List<EventOutcome>) : EventOutcome

// ─── ActiveEvent i wyniki sesji eventowej ─────────────────────────────────────

data class ActiveEvent(
    val event: Event,
    val currentNode: EventNode
)

sealed interface ActiveEventResult {
    data class Continue(val activeEvent: ActiveEvent) : ActiveEventResult
    data class Finished(val endResult: EventEndResult) : ActiveEventResult
}
