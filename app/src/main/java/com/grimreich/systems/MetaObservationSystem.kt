package com.grimreich.systems

import com.grimreich.core.GameConstants
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.PersistentMeta
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

        // --- QUANTUM SCAN: Unity of Seven Selves ---
        when (questId) {
            "q_blood_8" -> uniteSelf(state, PersistentMeta.SelfAspect.WRATH)
            "q_verdict_5" -> uniteSelf(state, PersistentMeta.SelfAspect.SHADOW)
            "q_meta_1" -> uniteSelf(state, PersistentMeta.SelfAspect.LIGHT)
            "q_lost_apostle" -> uniteSelf(state, PersistentMeta.SelfAspect.FEAR)
            "q_scribes_1" -> uniteSelf(state, PersistentMeta.SelfAspect.HOPE)
            "q_void_whisper" -> uniteSelf(state, PersistentMeta.SelfAspect.EMPTINESS)
            "q_lost_scribe" -> uniteSelf(state, PersistentMeta.SelfAspect.PEACE)
        }

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
            completed >= GameConstants.META_QUEST_THRESHOLD && !state.quest.worldFlags.contains("meta_chain_unlock") -> {
                state.quest.worldFlags.add("meta_chain_unlock")
                if (questEngine.getStatus("q_meta_1", state) == QuestStatus.AVAILABLE) {
                    questEngine.activateQuestDirect(state, "q_meta_1")
                }
            }
        }
    }

    private fun uniteSelf(state: GameState, aspect: PersistentMeta.SelfAspect) {
        if (state.persistentMeta.unitedSelves.add(aspect)) {
            state.logEntries.add("SIEDEM SELVES: Aspekt ${aspect.name} zintegrowany z Kotwicą.")
            if (state.persistentMeta.unitedSelves.size >= 7) {
                state.logEntries.add("!!! KONWERGENCJA KOMPLETNA: Siedem wersji Ja stało się Jednością !!!")
                state.metaAwarenessLevel += 3
            }
        }
    }
}
