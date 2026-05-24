package com.darklandsmobile.core

data class SaveState(
    val playerState: PlayerState,
    val lastResolutionSummary: String? = null,
    val version: Int = 1
)

data class QuestJournalState(
    val activeQuestId: String?,
    val completedQuestIds: List<String>,
    val currentCityId: String,
    val entries: List<QuestLogEntry>
)
