package com.darklandsmobile.systems

import com.darklandsmobile.core.PlayerState
import com.darklandsmobile.core.QuestJournalState
import com.darklandsmobile.core.QuestLogEntry

object QuestJournalSystem {
    fun build(playerState: PlayerState): QuestJournalState {
        val entries = QuestSystem.all().map { quest ->
            val status = when {
                quest.id == playerState.activeQuestId -> "ACTIVE"
                quest.id in playerState.completedQuestIds -> "COMPLETED"
                else -> quest.status.name
            }
            QuestLogEntry(
                questId = quest.id,
                title = quest.title,
                status = status,
                notes = "City: ${quest.cityId}, reward: ${quest.rewardGold} gold"
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