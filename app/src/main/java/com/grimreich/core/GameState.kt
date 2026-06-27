package com.grimreich.core

import com.grimreich.grimreich.v1.*

data class GameState(
    @Transient val grimEngine: GrimWorldEngine = GrimWorldEngineFactory.create(),

    var playerName: String? = null,
    var heroName: String? = null,
    var characterNameLocked: Boolean = false,
    var metaAwarenessLevel: Int = 0,

    var grimCurrentRegion: String = "wybrzeze_polnocne",
    var grimPendingExpeditionName: String? = null,
    var pendingQuestId: String? = null,
    var pendingDialogueNpcName: String? = null,
    var pendingDialogueNpcRole: String? = null,
    var pendingDialogueNodeId: String? = null,

    val party: MutableList<Hero> = mutableListOf(),
    val hireableHeroes: MutableList<Hero> = mutableListOf(),
    var activeHeroId: String? = null,
    val inventory: MutableList<Item> = mutableListOf(),
    val logEntries: MutableList<String> = mutableListOf(),
    var gold: Int = 100,

    var quest: QuestState = QuestState(),
    val reputation: ReputationState = ReputationState(),
    val prayer: PrayerState = PrayerState(),
    val world: WorldState = WorldState(),
    val combat: CombatState = CombatState(),
    val knownNpcs: MutableMap<String, List<NPC>> = mutableMapOf(),
    val unlockedLoreIds: MutableSet<String> = mutableSetOf(),
    val persistentMeta: PersistentMeta = PersistentMeta(),
    var isExpeditionActive: Boolean = false,
    var lastSaveTimestamp: Long = System.currentTimeMillis()
) {
    fun deepCopy(): GameState = GameState(
        grimEngine = GrimWorldEngineFactory.create(),
        playerName = playerName,
        heroName = heroName,
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
        quest = QuestState(
            activeQuestIds = quest.activeQuestIds.toMutableSet(),
            completedQuestIds = quest.completedQuestIds.toMutableSet(),
            progress = quest.progress.mapValues { it.value.copy(variables = it.value.variables.toMap()) }.toMutableMap()
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
        knownNpcs = knownNpcs.mapValues { it.value.map { n -> n.copy() } }.toMutableMap(),
        unlockedLoreIds = unlockedLoreIds.toMutableSet(),
        persistentMeta = persistentMeta.copy(
            unlockedLegacyBuffs = persistentMeta.unlockedLegacyBuffs.toMutableSet()
        ),
        isExpeditionActive = isExpeditionActive,
        lastSaveTimestamp = lastSaveTimestamp
    )
}
