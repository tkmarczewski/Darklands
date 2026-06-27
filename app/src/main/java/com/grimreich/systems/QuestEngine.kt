package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.QuestStatus
import com.grimreich.core.QuestProgress
import javax.inject.Inject
import javax.inject.Singleton

enum class StepType { COMBAT, DIALOGUE, INVESTIGATION }

data class QuestStep(
    val description: String,
    val type: StepType,
    val targetId: String // e.g. NPC ID or Enemy ID
)

data class QuestDefinition(
    val id: String,
    val title: String,
    val description: String,
    val rewardGold: Int,
    val steps: List<QuestStep>,
    val cityId: String,
    val originNpcId: String
)

@Singleton
class QuestEngine @Inject constructor(
    private val gameRepository: GameRepository
) {
    private val registry = mutableMapOf<String, QuestDefinition>()

    fun register(definition: QuestDefinition) {
        registry[definition.id] = definition
    }

    fun getDefinition(id: String) = registry[id]

    fun getStatus(questId: String): QuestStatus {
        return gameRepository.currentState().quest.progress[questId]?.status ?: QuestStatus.LOCKED
    }

    fun activateQuest(questId: String) {
        gameRepository.updateState { state ->
            val p = state.quest.progress.getOrPut(questId) { QuestProgress(questId) }
            if (p.status == QuestStatus.AVAILABLE || p.status == QuestStatus.LOCKED) {
                p.status = QuestStatus.ACTIVE
                state.quest.activeQuestIds.add(questId)
            }
        }
    }

    fun advanceStep(questId: String) {
        gameRepository.updateState { state ->
            val p = state.quest.progress[questId] ?: return@updateState
            val def = registry[questId] ?: return@updateState
            
            if (p.currentStepIndex < def.steps.size - 1) {
                p.currentStepIndex++
            } else {
                p.status = QuestStatus.OBJECTIVE_MET
            }
        }
    }

    fun completeQuest(questId: String) {
        gameRepository.updateState { state ->
            val p = state.quest.progress[questId] ?: return@updateState
            val def = registry[questId] ?: return@updateState
            
            if (p.status == QuestStatus.OBJECTIVE_MET) {
                p.status = QuestStatus.COMPLETED
                state.quest.activeQuestIds.remove(questId)
                state.quest.completedQuestIds.add(questId)
                state.gold += def.rewardGold
                state.logEntries.add("Ukończono zadanie: ${def.title}. Otrzymano ${def.rewardGold} G.")
            }
        }
    }
    
    fun getActiveQuestsForCity(cityId: String): List<QuestDefinition> {
        val activeIds = gameRepository.currentState().quest.activeQuestIds
        return registry.values.filter { it.id in activeIds && it.cityId == cityId }
    }
}
