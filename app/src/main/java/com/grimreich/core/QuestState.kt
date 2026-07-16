package com.grimreich.core

import kotlinx.serialization.Serializable

@Serializable
enum class QuestStatus {
    LOCKED, AVAILABLE, ACTIVE, OBJECTIVE_MET, COMPLETED, FAILED
}

@Serializable
enum class StepType { COMBAT, DIALOGUE, INVESTIGATION, SOCIAL, META, EXPEDITION }

@Serializable
enum class QuestCategory { COMBAT, SOCIAL, INVESTIGATION, MIXED, META, ANOMALY, DRAMA, BEAST, INTRIGUE }

@Serializable
data class QuestProgress(
    val questId: String,
    var status: QuestStatus = QuestStatus.LOCKED,
    var currentStepIndex: Int = 0,
    val variables: Map<String, Int> = emptyMap(),
    val startedOnDay: Int = 0,
    val lastAdvancedOnDay: Int = 0
)

@Serializable
data class QuestState(
    val activeQuestIds: MutableSet<String> = mutableSetOf(),
    val completedQuestIds: MutableSet<String> = mutableSetOf(),
    val failedQuestIds: MutableSet<String> = mutableSetOf(),
    val progress: MutableMap<String, QuestProgress> = mutableMapOf(),
    val worldFlags: MutableSet<String> = mutableSetOf()
)

