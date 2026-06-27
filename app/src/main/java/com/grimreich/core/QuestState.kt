package com.grimreich.core

import kotlinx.serialization.Serializable

@Serializable
enum class QuestStatus {
    LOCKED, AVAILABLE, ACTIVE, OBJECTIVE_MET, COMPLETED, FAILED
}

@Serializable
data class QuestProgress(
    val questId: String,
    var status: QuestStatus = QuestStatus.LOCKED,
    var currentStepIndex: Int = 0,
    val variables: Map<String, Int> = emptyMap()
)

@Serializable
data class QuestState(
    val activeQuestIds: MutableSet<String> = mutableSetOf(),
    val completedQuestIds: MutableSet<String> = mutableSetOf(),
    val progress: MutableMap<String, QuestProgress> = mutableMapOf()
)
