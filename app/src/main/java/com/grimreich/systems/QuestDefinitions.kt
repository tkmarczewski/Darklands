package com.grimreich.systems

enum class QuestStepType {
    COMBAT,
    DIALOGUE,
    INVESTIGATION,
    TRAVEL,
    CHOICE,
    REPORT_BACK
}

data class QuestStep(
    val id: String,
    val type: QuestStepType,
    val targetId: String,
    val description: String,
    val meta: Map<String, String> = emptyMap()
)

data class QuestDefinition(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val baseReward: Int,
    val steps: List<QuestStep>
)
