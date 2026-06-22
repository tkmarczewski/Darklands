package com.grimreich.systems

object QuestDefinitionRegistry {

    val allDefinitions: List<QuestDefinition> = listOf(
        // Placeholder to resolve warnings while building
        QuestDefinition("test_quest", "Test Quest", "Testing chain logic", "Drama", 100, listOf(
            QuestStep("test_step_1", QuestStepType.DIALOGUE, "npc_test", "Start test.")
        ))
    )

    fun getById(id: String): QuestDefinition? = allDefinitions.find { it.id == id }
}
