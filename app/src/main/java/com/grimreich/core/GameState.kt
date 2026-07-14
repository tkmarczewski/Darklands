package com.grimreich.core

import com.grimreich.grimreich.v1.NPC
import com.grimreich.grimreich.v1.Item

sealed interface PendingWorldAction {
    data object None : PendingWorldAction
    data class ResolveQuest(val questId: String) : PendingWorldAction
    data class QuestCombatWin(val questId: String) : PendingWorldAction
    data class Dialogue(
        val npcName: String,
        val npcRole: String,
        val nodeId: String,
        val relatedQuestId: String? = null
    ) : PendingWorldAction
}

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
            val trimmed = logEntries.takeLast(100)
            logEntries.clear()
            logEntries.addAll(trimmed)
        }
    }

    fun normalizeState() {
        gold = gold.coerceAtLeast(0)

        world.day = world.day.coerceAtLeast(1)
        world.fatigue = world.fatigue.coerceAtLeast(0)
        world.globalStability = world.globalStability.coerceIn(0, 100)
        world.echoIntensity = world.echoIntensity.coerceIn(0f, 1f)
        world.collapseProgress = world.collapseProgress.coerceIn(0f, 1f)

        // Project Anchor: Ensure anchorIdentity is synced with playerName
        if (persistentMeta.anchorIdentity == null && playerName != null) {
            persistentMeta.anchorIdentity = playerName
        }

        party.forEach { it.normalize() }

        val currentActive = party.find { it.id == activeHeroId }
        if (currentActive == null || currentActive.isDead) {
            activeHeroId = party.firstOrNull { !it.isDead }?.id
        }

        trimLogs()
    }

    fun deepCopy(): GameState = this.copy(
        party = this.party.map { it.deepCopy() }.toMutableList(),
        hireableHeroes = this.hireableHeroes.map { it.deepCopy() }.toMutableList(),
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
            log = this.combat.log.toMutableList(),
            initiativeOrder = this.combat.initiativeOrder.toMutableList()
        ),
        knownNpcs = this.knownNpcs.mapValues { it.value.map { n -> n.deepCopy() } }.toMutableMap(),
        unlockedLoreIds = this.unlockedLoreIds.toMutableSet(),
        persistentMeta = this.persistentMeta.copy(
            unlockedLegacyBuffs = this.persistentMeta.unlockedLegacyBuffs.toMutableSet(),
            unitedSelves = this.persistentMeta.unitedSelves.toMutableSet()
        ),
        grantedRewardFlags = this.grantedRewardFlags.toMutableSet(),
        companionShadows = this.companionShadows.map { it.deepCopy() }.toMutableList()
    )
}
