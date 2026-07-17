package com.grimreich.core

import kotlinx.serialization.Serializable

@Serializable
enum class QuestStatus {
    locked, available, active, objective_met, completed, failed
}

@Serializable
enum class StepType { combat, dialogue, investigation, social, meta, expedition }

@Serializable
enum class QuestCategory { combat, social, investigation, mixed, meta, anomaly, drama, beast, intrigue }

@Serializable
data class QuestProgress(
    val questId: String,
    var status: QuestStatus = QuestStatus.locked,
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

