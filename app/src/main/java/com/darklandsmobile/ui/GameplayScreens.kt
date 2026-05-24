package com.darklandsmobile.ui

import com.darklandsmobile.core.CityScreenState
import com.darklandsmobile.core.QuestJournalState
import com.darklandsmobile.core.ResolutionScreenState
import com.darklandsmobile.core.TravelScreenState

/**
 * Compose-ready text renderers that can be converted into real @Composable screens.
 * They stay framework-light so they can compile even before Android UI wiring is restored.
 */
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
            appendLine("- ${entry.questId}: ${entry.title} [${entry.status}] ${entry.rewardGold}g @ ${entry.cityId}")
        }
    }
}
