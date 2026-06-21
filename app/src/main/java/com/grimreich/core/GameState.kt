package com.grimreich.core

import com.grimreich.grimreich.v1.*

data class GameState(
    @Transient val grimEngine: GrimWorldEngine = GrimWorldEngineFactory.create(),

    var playerName: String? = null,
    var characterNameLocked: Boolean = false,
    var metaAwarenessLevel: Int = 0,

    var grimCurrentRegion: String = "wybrzeze_polnocne",
    var grimPendingExpeditionName: String? = null,
    var pendingQuestId: String? = null,
    var pendingDialogueNpcName: String? = null,
    var pendingDialogueNpcRole: String? = null,
    var pendingDialogueNodeId: String? = null,

    // Core game state
    val party: MutableList<Hero> = mutableListOf(),
    val hireableHeroes: MutableList<Hero> = mutableListOf(),
    var activeHeroId: String? = null,
    val inventory: MutableList<Item> = mutableListOf(),
    val logEntries: MutableList<String> = mutableListOf(),
    var gold: Int = 100,

    val quest: QuestState = QuestState(),
    val reputation: ReputationState = ReputationState(),
    val prayer: PrayerState = PrayerState(),
    val world: WorldState = WorldState(),
    val combat: CombatState = CombatState(),
    var lastSaveTimestamp: Long = System.currentTimeMillis()
) {
    fun deepCopy(): GameState = GameState(
        grimEngine = GrimWorldEngineFactory.create(), // Isolate engine by creating fresh for snapshot
        playerName = playerName,
        characterNameLocked = characterNameLocked,
        metaAwarenessLevel = metaAwarenessLevel,
        grimCurrentRegion = grimCurrentRegion,
        grimPendingExpeditionName = grimPendingExpeditionName,
        pendingQuestId = pendingQuestId,
        pendingDialogueNpcName = pendingDialogueNpcName,
        pendingDialogueNpcRole = pendingDialogueNpcRole,
        pendingDialogueNodeId = pendingDialogueNodeId,
        party = party.map { it.copy() }.toMutableList(),
        hireableHeroes = hireableHeroes.map { it.copy() }.toMutableList(),
        activeHeroId = activeHeroId,
        inventory = inventory.map { it.copy() }.toMutableList(),
        logEntries = logEntries.toMutableList(),
        gold = gold,
        quest = quest.copy(
            activeQuests = quest.activeQuests.toMutableList(),
            completedQuests = quest.completedQuests.toMutableList(),
            questProgress = quest.questProgress.toMutableMap(),
            activeEndgameQuests = quest.activeEndgameQuests.toMutableList(),
            completedEndgameQuests = quest.completedEndgameQuests.toMutableList()
        ),
        reputation = reputation.copy(
            cityFactions = reputation.cityFactions.mapValues { it.value.toMutableMap() }.toMutableMap()
        ),
        prayer = prayer.copy(
            blessings = prayer.blessings.toMutableList()
        ),
        world = world.copy(
            discoveredLocations = world.discoveredLocations.toMutableList()
        ),
        combat = combat.copy(
            log = combat.log.toMutableList(),
            enemyEffects = combat.enemyEffects.toMutableList(),
            heroEffects = combat.heroEffects.toMutableList()
        ),
        lastSaveTimestamp = lastSaveTimestamp
    )
}
