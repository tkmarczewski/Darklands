package com.grimreich.core

import com.grimreich.grimreich.v1.*

data class GameState(
    @Transient val grimEngine: GrimWorldEngine = GrimWorldEngineFactory.create(),
    var grimCurrentRegion: String = "Wybrzeże Północne",
    var grimPendingExpeditionName: String? = null,

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
    val combat: CombatState = CombatState()
) {
    fun deepCopy(): GameState = GameState(
        grimEngine = grimEngine,
        grimCurrentRegion = grimCurrentRegion,
        grimPendingExpeditionName = grimPendingExpeditionName,
        party = party.toMutableList(),
        activeHeroId = activeHeroId,
        inventory = inventory.toMutableList(),
        logEntries = logEntries.toMutableList(),
        gold = gold,
        quest = quest.copy(
            activeQuests = quest.activeQuests.toMutableList(),
            completedQuests = quest.completedQuests.toMutableList(),
            questProgress = quest.questProgress.toMutableMap()
        ),
        reputation = reputation.copy(
            city = reputation.city.toMutableMap()
        ),
        prayer = prayer.copy(),
        world = world.copy(),
        combat = combat.copy(
            log = combat.log.toMutableList(),
            enemyEffects = combat.enemyEffects.toMutableList(),
            heroEffects = combat.heroEffects.toMutableList()
        )
    )
}
