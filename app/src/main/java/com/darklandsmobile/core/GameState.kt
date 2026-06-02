package com.darklandsmobile.core

data class QuestState(
    val activeQuests: MutableList<String> = mutableListOf(),
    val completedQuests: MutableList<String> = mutableListOf(),
    val questProgress: MutableMap<String, Int> = mutableMapOf()
)

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
    val quest: QuestState = QuestState()
)

/**
 * Głęboka kopia stanu gry — zapobiega płytka kopiowaniu mutowalnych kolekcji.
 * Bez tego notify() współdzieli referencje do list/map/heroów,
 * przez co zapis i aktywny stan mogą się nadpisywać.
 */
fun GameState.deepCopy(): GameState = GameState(
    world = world.copy(),
    party = party.map { hero ->
        hero.copy(
            skills = hero.skills.toMutableMap(),
            equipment = mutableMapOf(
                "weapon" to hero.equipment["weapon"],
                "armor" to hero.equipment["armor"],
                "helmet" to hero.equipment["helmet"]
            )
        )
    }.toMutableList(),
    inventory = inventory.map { it.copy(effects = it.effects.toMap()) }.toMutableList(),
    activeHeroId = activeHeroId,
    logEntries = logEntries.toMutableList(),
    combat = combat.copy(log = combat.log.toMutableList()),
    prayer = prayer.copy(),
    reputation = reputation.copy(city = reputation.city.toMutableMap()),
    gold = gold,
    quest = quest.copy(
        activeQuests = quest.activeQuests.toMutableList(),
        completedQuests = quest.completedQuests.toMutableList(),
        questProgress = quest.questProgress.toMutableMap()
    )
)