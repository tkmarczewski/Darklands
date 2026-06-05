package com.grimreich.ui

import com.grimreich.core.RegionalSliceViewData

object RegionalSliceScreen {
    fun render(state: RegionalSliceViewData): String = buildString {
        appendLine("=== ${state.cityTitle.uppercase()} SLICE ===")
        appendLine("Background: ${state.backgroundUrl}")
        appendLine("Reference: ${state.referenceTitle} (${state.sourceLabel})")
        appendLine("Mood: ${state.moodText}")
        appendLine("Gold: ${state.gold}")
        appendLine("Active quest: ${state.activeQuestId ?: "none"}")
        appendLine("Featured contracts:")
        state.quests.forEach { quest ->
            appendLine("- ${quest.questId}: ${quest.title} | ${quest.rewardGold}g | ${quest.difficultyLabel}")
            appendLine("  ${quest.shortBrief}")
        }
    }
}
