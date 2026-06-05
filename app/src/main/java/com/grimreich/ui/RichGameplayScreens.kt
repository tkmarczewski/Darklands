package com.grimreich.ui

import com.grimreich.core.CityHubViewData

object RichGameplayScreens {
    fun renderCityHub(state: CityHubViewData): String = buildString {
        appendLine("=== ${state.cityTitle.uppercase()} HUB ===")
        appendLine("Backdrop: ${state.backdropName}")
        appendLine("Emblem: ${state.emblemName}")
        appendLine("Mood: ${state.moodText}")
        appendLine("Gold: ${state.gold}")
        appendLine("Active quest: ${state.activeQuestId ?: "none"}")
        appendLine("Quest board:")
        if (state.questCards.isEmpty()) {
            appendLine("- No contracts posted.")
        } else {
            state.questCards.forEach { card ->
                appendLine("- ${card.questId}: ${card.title} [${card.status}] ${card.rewardGold}g | ${card.accentLabel}")
                appendLine("  ${card.flavorText}")
            }
        }
    }
}
