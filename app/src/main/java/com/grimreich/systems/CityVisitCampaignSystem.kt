package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.QuestProgress
import com.grimreich.core.QuestStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityVisitCampaignSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val questEngine: QuestEngine
) {
    fun onCityEntered(cityId: String) {
        val state = gameRepository.currentState()
        if (cityId != "opactwo_ciszy") return

        val current = state.quest.progress["meta_verdict_visits"]?.variables?.get("count") ?: 0
        val next = current + 1

        state.quest.progress["meta_verdict_visits"] = QuestProgress(
            questId = "meta_verdict_visits",
            status = QuestStatus.ACTIVE,
            variables = mapOf("count" to next)
        )

        when (next) {
            1 -> state.logEntries.add("Strażnik wspomina o ciele bez ran w gabinecie. Na ścianie: WYROK WYKONANY.")
            3 -> state.logEntries.add("Lira Voss zniknęła. Na drzwiach jej mieszkania widnieje runa: WYMAZANA.")
            5 -> state.logEntries.add("W ruinach fabryki wypalono słowo: WINNI.")
            7 -> {
                state.quest.worldFlags.add("verdict_campaign_ready")
                if (questEngine.getStatus("q_verdict_1", state) == QuestStatus.AVAILABLE) {
                    state.logEntries.add("Ravenn chce z tobą mówić o serii 'beztwarzowych wyroków'.")
                }
            }
        }
    }
}
