package com.grimreich.ui

import com.grimreich.core.CityScreenState
import com.grimreich.core.QuestJournalState
import com.grimreich.core.ResolutionScreenState
import com.grimreich.core.TravelScreenState

object GameplayScreens {

    fun renderCity(state: CityScreenState): String = buildString {
        appendLine("=== CITY: ${state.cityId.uppercase()} ===")
        appendLine("Gold: ${state.gold}")
        appendLine("Active quest: ${state.activeQuestId ?: "none"}")
        appendLine("Available quests:")
        if (state.availableQuests.isEmpty()) {
            appendLine("- none")
        } else {
            state.availableQuests.forEach { quest ->
                appendLine("- ${quest.id}: ${quest.title} (${quest.rewardGold}g)")
            }
        }
    }

    fun renderTravel(state: TravelScreenState): String = buildString {
        appendLine("=== TRAVEL ===")
        appendLine("From: ${state.fromCityId}")
        appendLine("To: ${state.toCityId}")
        appendLine("Hours traveled: ${state.totalHoursTraveled}")
        appendLine("Fatigue: ${state.fatigue}")
        appendLine("Last encounter: ${state.lastEncounterId ?: "none"}")
    }

    fun renderResolution(state: ResolutionScreenState): String = buildString {
        appendLine("=== QUEST RESOLVED ===")
        appendLine("Quest: ${state.questId}")
        appendLine("City: ${state.cityId}")
        appendLine("Gold: ${state.goldBefore} -> ${state.goldAfter}")
        appendLine("Reputation: ${state.reputationAfter}")
        appendLine(state.summary)
    }

    fun renderJournal(state: QuestJournalState): String = buildString {
        appendLine("=== JOURNAL ===")
        appendLine("Current city: ${state.currentCityId}")
        appendLine("Active quest: ${state.activeQuestId ?: "none"}")
        appendLine("Completed quests: ${state.completedQuestIds.size}")
        appendLine("Entries:")
        state.entries.forEach { entry ->
            appendLine("- ${entry.questId}: ${entry.title} [${entry.status}] ${entry.notes}")
        }
    }
}