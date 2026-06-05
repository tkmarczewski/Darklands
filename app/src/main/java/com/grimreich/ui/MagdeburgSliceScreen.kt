package com.grimreich.ui

import com.grimreich.core.GrimholdSliceViewData

object GrimholdSliceScreen {
    fun render(state: GrimholdSliceViewData): String = buildString {
        appendLine("=== MAGDEBURG VERTICAL SLICE ===")
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
