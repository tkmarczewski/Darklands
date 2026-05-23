package com.darklandsmobile.core

data class GameState(
    val party: MutableList<Hero> = mutableListOf(),
    var activeHeroId: String = "",
    val inventory: MutableList<Item> = mutableListOf(),
    val world: WorldState = WorldState(),
    val combat: CombatState = CombatState(),
    val quest: QuestState = QuestState(),
    val prayer: PrayerState = PrayerState(),
    val reputation: ReputationState = ReputationState(),
    val logEntries: MutableList<String> = mutableListOf(),
    // Ekonomia — Goldene Kronen (waluta oryginalu)
    var gold: Int = 50,
    var partyStash: MutableMap<String, Int> = mutableMapOf() // przechowywanie u karczmarz
)
