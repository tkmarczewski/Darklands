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
    val targetId: String
)

data class QuestDefinition(
    val id: String,
    val title: String,
    val description: String,
    val rewardGold: Int,
    val steps: List<QuestStep>,
    val cityId: String,
    val originNpcId: String,
    val prerequisiteQuestId: String? = null
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

    fun getStatus(questId: String, visited: MutableSet<String> = mutableSetOf()): QuestStatus {
        if (!visited.add(questId)) return QuestStatus.LOCKED
        
        val state = gameRepository.currentState()
        
        // 1. Check if completed
        if (state.quest.completedQuestIds.contains(questId)) return QuestStatus.COMPLETED
        
        // 2. Check if active
        val progress = state.quest.progress[questId]
        if (progress != null) return progress.status
        
        // 3. Chain logic: Check prerequisites
        val def = registry[questId]
        if (def?.prerequisiteQuestId != null) {
            val prereqStatus = getStatus(def.prerequisiteQuestId, visited)
            return if (prereqStatus == QuestStatus.COMPLETED) QuestStatus.AVAILABLE else QuestStatus.LOCKED
        }

        return QuestStatus.AVAILABLE
    }

    fun activateQuest(questId: String) {
        if (getStatus(questId) != QuestStatus.AVAILABLE) return

        gameRepository.updateState { state ->
            if (!state.quest.activeQuestIds.contains(questId)) {
                state.quest.activeQuestIds.add(questId)
            }
            state.quest.progress[questId] = QuestProgress(questId = questId, status = QuestStatus.ACTIVE)
        }
    }

    fun advanceStep(questId: String) {
        gameRepository.updateState { state ->
            val p = state.quest.progress[questId] ?: return@updateState
            if (p.status != QuestStatus.ACTIVE) return@updateState // Guard

            val def = registry[questId] ?: return@updateState
            
            if (p.currentStepIndex < def.steps.size - 1) {
                state.quest.progress[questId] = p.copy(currentStepIndex = p.currentStepIndex + 1)
            } else {
                state.quest.progress[questId] = p.copy(status = QuestStatus.OBJECTIVE_MET)
            }
        }
    }

    fun completeQuest(questId: String) {
        gameRepository.updateState { state ->
            val p = state.quest.progress[questId] ?: return@updateState
            val def = registry[questId] ?: return@updateState
            
            if (p.status == QuestStatus.OBJECTIVE_MET) {
                state.quest.progress[questId] = p.copy(status = QuestStatus.COMPLETED)
                state.quest.completedQuestIds.add(questId)
                state.quest.progress.remove(questId)
                state.quest.activeQuestIds.remove(questId)
                state.gold += def.rewardGold
                state.logEntries.add("Ukończono zadanie: ${def.title}. Otrzymano ${def.rewardGold} G.")
            }
        }
    }
    
    fun getAvailableQuestsForCity(cityId: String): List<QuestDefinition> {
        return registry.values.filter { it.cityId == cityId && getStatus(it.id) == QuestStatus.AVAILABLE }
    }

    fun getActiveQuestsForCity(cityId: String): List<QuestDefinition> {
        val activeIds = gameRepository.currentState().quest.activeQuestIds
        return registry.values.filter { it.id in activeIds && it.cityId == cityId }
    }
}
