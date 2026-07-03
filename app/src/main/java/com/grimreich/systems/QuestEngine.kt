package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
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

@Singleton
class QuestEngine @Inject constructor(
    private val gameRepository: GameRepository
) {
    private val registry = mutableMapOf<String, QuestDefinition>()

    fun register(definition: QuestDefinition) {
        registry[definition.id] = definition
    }

    fun clearRegistry() {
        registry.clear()
    }

    fun getDefinition(id: String) = registry[id]

    fun getStatus(questId: String, state: GameState? = null, visited: MutableSet<String> = mutableSetOf()): QuestStatus {
        val def = registry[questId] ?: return QuestStatus.LOCKED
        if (!visited.add(questId)) return QuestStatus.LOCKED

        val actualState = state ?: gameRepository.currentState()

        if (actualState.quest.completedQuestIds.contains(questId)) return QuestStatus.COMPLETED

        // FIX: Check progress map first (authoritative source of ACTIVE/OBJECTIVE_MET)
        val progress = actualState.quest.progress[questId]
        if (progress != null) return progress.status

        // FIX: Guard against activeQuestIds desync - if quest is tracked as active
        // but has no progress entry (corrupted state), treat it as ACTIVE to prevent
        // it from appearing as AVAILABLE again and being double-activated.
        if (actualState.quest.activeQuestIds.contains(questId)) return QuestStatus.ACTIVE

        if (def.prerequisiteQuestId != null) {
            val prereqStatus = getStatus(def.prerequisiteQuestId, actualState, visited)
            return if (prereqStatus == QuestStatus.COMPLETED) QuestStatus.AVAILABLE else QuestStatus.LOCKED
        }

        return QuestStatus.AVAILABLE
    }

    // --- STATE MUTATING METHODS ---

    /**
     * Activates a quest. Should be called within updateState.
     */
    fun activateQuestDirect(state: GameState, questId: String) {
        val status = getStatus(questId, state)
        if (status == QuestStatus.AVAILABLE) {
            if (!state.quest.activeQuestIds.contains(questId)) {
                state.quest.activeQuestIds.add(questId)
            }
            state.quest.progress[questId] = QuestProgress(questId = questId, status = QuestStatus.ACTIVE)
            state.logEntries.add("Nowe zadanie: ${registry[questId]?.title}")
        } else {
            // FIX (BUG-4): Add validation logging for unexpected status
            state.logEntries.add("⚠️ UWAGA: Zadanie $questId nie jest dostępne (status: $status)")
        }
    }

    fun activateQuest(questId: String) {
        gameRepository.updateState { activateQuestDirect(it, questId) }
    }

    fun advanceStepDirect(state: GameState, questId: String) {
        val p = state.quest.progress[questId] ?: return
        if (p.status != QuestStatus.ACTIVE) return
        val def = registry[questId] ?: return
        if (p.currentStepIndex < def.steps.size - 1) {
            state.quest.progress[questId] = p.copy(currentStepIndex = p.currentStepIndex + 1)
        } else {
            state.quest.progress[questId] = p.copy(status = QuestStatus.OBJECTIVE_MET)
        }
    }

    fun advanceStep(questId: String) {
        gameRepository.updateState { advanceStepDirect(it, questId) }
    }

    fun completeQuestDirect(state: GameState, questId: String) {
        val p = state.quest.progress[questId] ?: return
        val def = registry[questId] ?: return
        if (p.status == QuestStatus.OBJECTIVE_MET) {
            state.gold += def.rewardGold
            state.logEntries.add("Uko\u0144czono zadanie: ${def.title}. Otrzymano ${def.rewardGold} G.")
            state.quest.completedQuestIds.add(questId)
            state.quest.progress.remove(questId)
            state.quest.activeQuestIds.remove(questId)
        }
    }

    fun completeQuest(questId: String) {
        gameRepository.updateState { completeQuestDirect(it, questId) }
    }

    // --- QUERY METHODS ---

    fun getAvailableQuestsForCity(cityId: String): List<QuestDefinition> {
        return registry.values.filter { it.cityId == cityId && getStatus(it.id) == QuestStatus.AVAILABLE }
    }

    fun getActiveQuestsForCity(cityId: String): List<QuestDefinition> {
        val activeIds = gameRepository.currentState().quest.activeQuestIds
        return registry.values.filter { it.id in activeIds && it.cityId == cityId }
    }

    fun getAllRelevantQuestsForCity(cityId: String): List<QuestDefinition> {
        val state = gameRepository.currentState()
        return registry.values.filter {
            it.cityId == cityId && (it.id in state.quest.activeQuestIds || getStatus(it.id) == QuestStatus.AVAILABLE)
        }
    }

    fun validateQuestGraph(): List<String> {
        val issues = mutableListOf<String>()
        registry.values.forEach { def ->
            def.prerequisiteQuestId?.let { prereq ->
                if (registry[prereq] == null) {
                    issues.add("Quest ${def.id} ma brakuj\u0105cy prerequisite: $prereq")
                }
            }
        }
        return issues.distinct()
    }
}

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
