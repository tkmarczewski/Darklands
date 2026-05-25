package com.darklandsmobile.core

data class GameState(
    val world: WorldState = WorldState(),
    val party: MutableList<Hero> = mutableListOf(),
    val inventory: MutableList<Item> = mutableListOf(),
    var activeHeroId: String? = null,
    val logEntries: MutableList<String> = mutableListOf(),
    val combat: CombatState = CombatState(),
    val prayer: PrayerState = PrayerState(),
    val reputation: ReputationState = ReputationState()
)