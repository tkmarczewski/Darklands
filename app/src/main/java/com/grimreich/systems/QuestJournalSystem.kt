package com.grimreich.systems

import com.grimreich.core.PlayerState
import com.grimreich.core.QuestJournalState
import com.grimreich.core.QuestLogEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestJournalSystem @Inject constructor(
    private val questSystem: QuestSystem
) {
    fun build(playerState: PlayerState): QuestJournalState {
        val activeQuests = questSystem.all().filter { it.status == QuestStatus.AKTYWNE }
        
        val entries = activeQuests.map {
            QuestLogEntry(
                questId = it.id,
                title = it.title,
                status = "W TOKU",
                notes = it.description
            )
        }

        return QuestJournalState(
            activeQuestId = playerState.activeQuestId,
            completedQuestIds = playerState.completedQuestIds,
            currentCityId = playerState.currentCityId,
            entries = entries
        )
    }
}
