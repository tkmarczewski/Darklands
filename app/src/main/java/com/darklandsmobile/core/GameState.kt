package com.darklandsmobile.core

import com.darklandsmobile.grimreich.v1.GrimWorldEngine

data class GameState(
    val world: WorldState = WorldState(),
    val party: MutableList<Hero> = mutableListOf(),
    val inventory: MutableList<Item> = mutableListOf(),
    var activeHeroId: String? = null,
    val logEntries: MutableList<String> = mutableListOf(),
    val combat: CombatState = CombatState(),
    val prayer: PrayerState = PrayerState(),
    val reputation: ReputationState = ReputationState(),
    var gold: Int = 100,
    val quest: QuestState = QuestState(),
    val grimEngine: GrimWorldEngine = GrimWorldEngineFactory.create(),
    var grimCurrentRegion: String = "Nowe Wybrzeże",
    var grimPendingExpeditionName: String? = null
)

fun GameState.deepCopy(): GameState = GameState(
    world = world.copy(),
    party = party.map { it.copy() }.toMutableList(),
    inventory = inventory.map { it.copy() }.toMutableList(),
    activeHeroId = activeHeroId,
    logEntries = logEntries.toMutableList(),
    combat = combat.copy(),
    prayer = prayer.copy(),
    reputation = reputation.copy(),
    gold = gold,
    quest = quest.copy(),
    grimEngine = grimEngine,
    grimCurrentRegion = grimCurrentRegion,
    grimPendingExpeditionName = grimPendingExpeditionName
)
