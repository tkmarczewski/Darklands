package com.grimreich.core

data class PlayerState(
    val currentCityId: String = "",
    val gold: Int = 0,
    val prayer: Int = 0,
    val reputation: Int = 0,
    val activeQuestId: String? = null,
    val completedQuestIds: List<String> = emptyList(),
    val finalQuestSummary: String = "",
    val travelState: TravelPartyState = TravelPartyState()
)
