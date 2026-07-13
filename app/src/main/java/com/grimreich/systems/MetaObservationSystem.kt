package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.QuestStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetaObservationSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val chronicleSystem: ChronicleSystem,
    private val questEngine: QuestEngine
) {
    fun onQuestCompleted(questId: String) {
        val state = gameRepository.currentState()
        val completed = state.quest.completedQuestIds.size

        when {
            completed >= 4 && !state.quest.worldFlags.contains("meta_hint_1") -> {
                state.quest.worldFlags.add("meta_hint_1")
                state.metaAwarenessLevel += 1
                state.logEntries.add("Na marginesie Kroniki: 'Podmiot reaguje zgodnie z przewidywaniem.'")
                chronicleSystem.unlock("lore_scribes")
            }
            completed >= 9 && !state.quest.worldFlags.contains("meta_hint_2") -> {
                state.quest.worldFlags.add("meta_hint_2")
                state.metaAwarenessLevel += 1
                state.logEntries.add("Nie do niego. Do tego, który wybiera.")
            }
            completed >= 15 && !state.quest.worldFlags.contains("meta_hint_3") -> {
                state.quest.worldFlags.add("meta_hint_3")
                state.metaAwarenessLevel += 1
                state.logEntries.add("Kronika nie opisuje bohatera. Opisuje ciebie.")
            }
            completed >= 22 && !state.quest.worldFlags.contains("meta_hint_4") -> {
                state.quest.worldFlags.add("meta_hint_4")
                state.metaAwarenessLevel += 1
                state.logEntries.add("Archiwista bez twarzy zna twoje imię, lecz nie zna bohatera.")
            }
            completed >= 30 && !state.quest.worldFlags.contains("meta_chain_unlock") -> {
                state.quest.worldFlags.add("meta_chain_unlock")
                if (questEngine.getStatus("q_meta_1", state) == QuestStatus.AVAILABLE) {
                    questEngine.activateQuestDirect(state, "q_meta_1")
                }
            }
        }
    }
}
