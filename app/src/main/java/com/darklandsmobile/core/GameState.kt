package com.darklandsmobile.core

import com.darklandsmobile.systems.QuestEntry

data class PlayerState(
    val currentCityId: String = "magdeburg",
    val gold: Int = 100,
    val activeQuestId: String? = null,
    val completedQuestIds: List<String> = emptyList(),
    val travelState: TravelPartyState = TravelPartyState()
)

data class QuestLogEntry(
    val questId: String,
    val title: String,
    val status: String,
    val cityId: String,
    val rewardGold: Int
)

data class CityScreenState(
    val cityId: String,
    val availableQuests: List<QuestEntry>,
    val gold: Int,
    val activeQuestId: String?
)

data class TravelScreenState(
    val fromCityId: String,
    val toCityId: String,
    val totalHoursTraveled: Int,
    val fatigue: Int,
    val lastEncounterId: String?
)

data class ResolutionScreenState(
    val questId: String,
    val cityId: String,
    val goldBefore: Int,
    val goldAfter: Int,
    val reputationAfter: Int,
    val summary: String
)
