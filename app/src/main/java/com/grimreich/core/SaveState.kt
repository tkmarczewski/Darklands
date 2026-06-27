package com.grimreich.core

data class SaveState(
    val playerState: PlayerState = PlayerState(),
    val lastResolutionSummary: String? = null,
    val version: Int = 1
)

data class QuestJournalState(
    val activeQuestId: String?,
    val completedQuestIds: List<String>,
    val currentCityId: String
)
