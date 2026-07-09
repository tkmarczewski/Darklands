package com.grimreich.core

import com.grimreich.grimreich.v1.NPC
import com.grimreich.grimreich.v1.Item
import com.grimreich.grimreich.v1.GrimWorldEngine
import com.grimreich.grimreich.v1.GrimWorldEngineFactory

data class GameState(
    var grimEngine: GrimWorldEngine = GrimWorldEngineFactory.create(),

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
    var gold: Int = 0,

    var quest: QuestState = QuestState(),
    var reputation: ReputationState = ReputationState(),
    var prayer: PrayerState = PrayerState(),
    var world: WorldState = WorldState(),
    var combat: CombatState = CombatState(),
    val knownNpcs: MutableMap<String, List<NPC>> = mutableMapOf(),
    val unlockedLoreIds: MutableSet<String> = mutableSetOf(),
    var persistentMeta: PersistentMeta = PersistentMeta(),
    var isExpeditionActive: Boolean = false,
    var lastSaveTimestamp: Long = 0,

    var grimMutationPhase: Int = 0,
    val grantedRewardFlags: MutableSet<String> = mutableSetOf(),
    val companionShadows: MutableList<Hero> = mutableListOf()
) {
    fun trimLogs() {
        if (logEntries.size > 100) {
            val toRemove = logEntries.size - 100
            repeat(toRemove) { logEntries.removeAt(0) }
        }
    }

    fun normalizeState() {
        if (gold < 0) gold = 0
        if (world.day < 1) world.day = 1
        trimLogs()
        party.forEach { it.normalize() }
    }

    fun deepCopy(): GameState = this.copy(
        grimEngine = this.grimEngine, // Not deeply copied, system handled
        party = this.party.map { it.copy() }.toMutableList(),
        hireableHeroes = this.hireableHeroes.map { it.copy() }.toMutableList(),
        inventory = this.inventory.map { it.copy() }.toMutableList(),
        logEntries = this.logEntries.toMutableList(),
        quest = this.quest.copy(
            activeQuestIds = this.quest.activeQuestIds.toMutableSet(),
            completedQuestIds = this.quest.completedQuestIds.toMutableSet(),
            progress = this.quest.progress.mapValues { it.value.copy() }.toMutableMap()
        ),
        reputation = this.reputation.copy(
            globalFactions = this.reputation.globalFactions.toMutableMap(),
            cityFactions = this.reputation.cityFactions.mapValues { it.value.toMutableMap() }.toMutableMap()
        ),
        prayer = this.prayer.copy(
            blessings = this.prayer.blessings.toMutableList()
        ),
        world = this.world.copy(
            discoveredLocations = this.world.discoveredLocations.toMutableList()
        ),
        combat = this.combat.copy(
            enemyEffects = this.combat.enemyEffects.toMutableList(),
            heroEffects = this.combat.heroEffects.toMutableList(),
            log = this.combat.log.toMutableList()
        ),
        knownNpcs = this.knownNpcs.mapValues { it.value.toList() }.toMutableMap(),
        unlockedLoreIds = this.unlockedLoreIds.toMutableSet(),
        persistentMeta = this.persistentMeta.copy(
            unlockedLegacyBuffs = this.persistentMeta.unlockedLegacyBuffs.toMutableSet()
        ),
        grantedRewardFlags = this.grantedRewardFlags.toMutableSet(),
        companionShadows = this.companionShadows.map { it.copy() }.toMutableList()
    )
}
