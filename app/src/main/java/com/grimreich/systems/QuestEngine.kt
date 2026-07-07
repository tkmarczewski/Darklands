package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.QuestStatus
import com.grimreich.core.QuestProgress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestEngine @Inject constructor(
    private val gameRepositoryProvider: dagger.Lazy<GameRepository>,
    private val experienceSystemProvider: dagger.Lazy<ExperienceSystem>
) {
    private val gameRepository get() = gameRepositoryProvider.get()
    private val experienceSystem get() = experienceSystemProvider.get()
    private val registry = mutableMapOf<String, QuestDefinition>()

    fun register(definition: QuestDefinition) {
        registry[definition.id] = definition
    }

    fun clearRegistry() {
        registry.clear()
    }

    fun getDefinition(id: String) = registry[id]

    /**
     * Zwraca aktualny status questa.
     * [visited] chroni przed nieskończoną rekursją przy cyklicznych prerequisite'ach.
     */
    fun getStatus(questId: String, state: GameState? = null, visited: MutableSet<String> = mutableSetOf()): QuestStatus {
        val def = registry[questId] ?: return QuestStatus.LOCKED
        if (!visited.add(questId)) return QuestStatus.LOCKED

        val actualState = state ?: gameRepository.currentState()

        if (actualState.quest.completedQuestIds.contains(questId)) return QuestStatus.COMPLETED
        
        val progress = actualState.quest.progress[questId]
        if (progress != null) return progress.status

        // FIX (BUG-6): Synchronization check
        if (actualState.quest.activeQuestIds.contains(questId)) return QuestStatus.ACTIVE

        if (def.prerequisiteQuestId != null) {
            val prereqStatus = getStatus(def.prerequisiteQuestId, actualState, visited)
            return if (prereqStatus == QuestStatus.COMPLETED) QuestStatus.AVAILABLE else QuestStatus.LOCKED
        }

        return QuestStatus.AVAILABLE
    }

    fun failQuestDirect(state: GameState, questId: String) {
        val p = state.quest.progress[questId] ?: return
        state.quest.progress[questId] = p.copy(status = QuestStatus.FAILED)
        state.quest.activeQuestIds.remove(questId)
        state.logEntries.add("ZADANIE PRZERWANE: ${getDefinition(questId)?.title}")
    }

    fun failQuest(questId: String) {
        gameRepository.updateState { failQuestDirect(it, questId) }
    }

    // --- STATE MUTATING METHODS ---

    fun activateQuestDirect(state: GameState, questId: String) {
        val status = getStatus(questId, state)
        if (status == QuestStatus.AVAILABLE) {
            // Idempotent activation: only add if not already there
            if (!state.quest.activeQuestIds.contains(questId)) {
                state.quest.activeQuestIds.add(questId)
            }
            // Only create progress if it doesn't exist
            if (!state.quest.progress.containsKey(questId)) {
                state.quest.progress[questId] = QuestProgress(questId = questId, status = QuestStatus.ACTIVE)
                state.logEntries.add("Nowe zadanie: ${registry[questId]?.title}")
            }
        } else if (status == QuestStatus.ACTIVE) {
            // Already active, ensure it is in activeQuestIds (fix for BUG-6)
            if (!state.quest.activeQuestIds.contains(questId)) {
                state.quest.activeQuestIds.add(questId)
            }
        } else {
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
            state.logEntries.add("Ukończono zadanie: ${def.title}. Otrzymano ${def.rewardGold} G.")
            
            val xpReward = (def.rewardGold * 0.5f).toInt().coerceAtLeast(10)
            val xpMsgs = experienceSystem.addPartyXpDirect(state, xpReward)
            state.logEntries.addAll(xpMsgs)

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

    /**
     * Shows both AVAILABLE and ACTIVE quests. FIX: Visibility for player.
     */
    fun getAllRelevantQuestsForCity(cityId: String, state: GameState? = null): List<QuestDefinition> {
        val actualState = state ?: gameRepository.currentState()
        return registry.values.filter {
            it.cityId == cityId && (getStatus(it.id, actualState) == QuestStatus.AVAILABLE || getStatus(it.id, actualState) == QuestStatus.ACTIVE || getStatus(it.id, actualState) == QuestStatus.OBJECTIVE_MET)
        }
    }

    fun validateQuestGraph(): List<String> {
        val issues = mutableListOf<String>()
        registry.values.forEach { def ->
            def.prerequisiteQuestId?.let { prereq ->
                if (registry[prereq] == null) {
                    issues.add("Quest ${def.id} ma brakujący prerequisite: $prereq")
                }
            }
        }
        
        // Cycle detection through DFS
        fun hasCycle(questId: String, visited: MutableSet<String>, stack: MutableSet<String>): Boolean {
            if (stack.contains(questId)) return true
            if (visited.contains(questId)) return false
            
            visited.add(questId)
            stack.add(questId)
            
            val prereq = registry[questId]?.prerequisiteQuestId
            if (prereq != null && hasCycle(prereq, visited, stack)) return true
            
            stack.remove(questId)
            return false
        }
        
        registry.keys.forEach { qId ->
            if (hasCycle(qId, mutableSetOf(), mutableSetOf())) {
                issues.add("Wykryto cykl w grafie questów przy: $qId")
            }
        }

        return issues.distinct()
    }
}

enum class StepType { COMBAT, DIALOGUE, INVESTIGATION, SOCIAL }

enum class QuestCategory { COMBAT, SOCIAL, INVESTIGATION, MIXED }

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
    val prerequisiteQuestId: String? = null,
    val category: QuestCategory = QuestCategory.COMBAT,
    val recommendedLevel: Int = 1
)
