package com.darklandsmobile.core

data class PlayerState(
    val currentCityId: String = "magdeburg",
    val gold: Int = 100,
    val prayer: Int = 0,
    val reputation: Int = 0,
    val activeQuestId: String? = null,
    val completedQuestIds: List<String> = emptyList(),
    val finalQuestSummary: String = "",
    val questLog: List<QuestLogEntry> = emptyList(),
    val travelState: TravelState = TravelState()
)