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
        android.util.Log.i("QuestEngine", "[QUEST] REGISTERING: ${definition.id} for city ${definition.cityId}")
        registry[definition.id] = definition
    }

    fun clearRegistry() {
        android.util.Log.w("QuestEngine", "[QUEST] REGISTRY CLEARED!")
        registry.clear()
    }

    fun getDefinition(id: String) = registry[id]

    fun getStatus(questId: String, state: GameState? = null, visited: MutableSet<String> = mutableSetOf()): QuestStatus {
        val actualState = state ?: gameRepository.currentState()

        if (questId.startsWith("resurrect_")) {
            val hId = questId.removePrefix("resurrect_")
            val hero = actualState.party.find { it.id == hId }
            if (hero == null || !hero.isDead) return QuestStatus.LOCKED
            
            val corpseId = "corpse_${hero.id}"
            val hasCorpse = actualState.inventory.any { it.id == corpseId }
            return if (hasCorpse) QuestStatus.AVAILABLE else QuestStatus.LOCKED
        }

        val def = registry[questId] ?: run {
            android.util.Log.e("QuestEngine", "[QUEST] Status check failed: Registry missing ID $questId")
            return QuestStatus.LOCKED
        }
        if (!visited.add(questId)) {
            android.util.Log.e("QuestEngine", "[QUEST] Circular dependency detected for $questId")
            return QuestStatus.LOCKED
        }

        if (actualState.quest.completedQuestIds.contains(questId)) return QuestStatus.COMPLETED
        
        val progress = actualState.quest.progress[questId]
        if (progress != null) {
            return progress.status
        }

        if (actualState.quest.activeQuestIds.contains(questId)) {
            return QuestStatus.ACTIVE
        }

        if (def.prerequisiteQuestId != null) {
            val preStatus = getStatus(def.prerequisiteQuestId, actualState, visited)
            if (preStatus != QuestStatus.COMPLETED) return QuestStatus.LOCKED
        }

        return QuestStatus.AVAILABLE
    }

    fun activateQuest(questId: String) {
        activateQuestDirect(gameRepository.currentState(), questId)
    }

    fun activateQuestDirect(state: GameState, questId: String) {
        if (getStatus(questId, state) != QuestStatus.AVAILABLE) return
        
        state.quest.activeQuestIds.add(questId)
        state.quest.progress[questId] = QuestProgress(questId = questId, status = QuestStatus.ACTIVE)
        android.util.Log.i("QuestEngine", "[QUEST] ACTIVATED: $questId")
    }

    fun advanceStep(questId: String) {
        advanceStepDirect(gameRepository.currentState(), questId)
    }

    fun advanceStepDirect(state: GameState, questId: String) {
        val p = state.quest.progress[questId] ?: return
        if (p.status != QuestStatus.ACTIVE) return
        val def = registry[questId] ?: return
        
        if (p.currentStepIndex < def.steps.size - 1) {
            state.quest.progress[questId] = p.copy(currentStepIndex = p.currentStepIndex + 1)
        } else {
            state.quest.progress[questId] = p.copy(status = QuestStatus.OBJECTIVE_MET)
            state.logEntries.add("CEL OSIĄGNIĘTY: ${def.title}")
        }
    }

    fun completeQuest(questId: String) {
        completeQuestDirect(gameRepository.currentState(), questId)
    }

    fun completeQuestDirect(state: GameState, questId: String) {
        val p = state.quest.progress[questId] ?: return
        if (p.status != QuestStatus.OBJECTIVE_MET && p.status != QuestStatus.ACTIVE) return
        
        val def = registry[questId] ?: return
        state.quest.activeQuestIds.remove(questId)
        state.quest.completedQuestIds.add(questId)
        state.quest.progress[questId] = p.copy(status = QuestStatus.COMPLETED)
        
        state.gold += def.rewardGold
        experienceSystem.addPartyXpDirect(state, def.recommendedLevel * 50)
        
        state.logEntries.add("ZADANIE UKOŃCZONE: ${def.title}. Nagroda: ${def.rewardGold} zł.")
        android.util.Log.i("QuestEngine", "[QUEST] COMPLETED: $questId")
    }

    fun failQuestDirect(state: GameState, questId: String) {
        state.quest.activeQuestIds.remove(questId)
        state.quest.progress[questId]?.let {
            state.quest.progress[questId] = it.copy(status = QuestStatus.FAILED)
        }
        android.util.Log.w("QuestEngine", "[QUEST] FAILED: $questId")
    }

    fun getCurrentObjective(questId: String, state: GameState? = null): String {
        val def = registry[questId] ?: return "???"
        val actualState = state ?: gameRepository.currentState()
        val p = actualState.quest.progress[questId] ?: return def.description
        
        return if (p.status == QuestStatus.OBJECTIVE_MET) {
            "Wróć do: ${def.originNpcId.uppercase()}"
        } else {
            def.steps.getOrNull(p.currentStepIndex)?.description ?: "???"
        }
    }

    fun getActiveQuestsForCity(cityId: String): List<QuestDefinition> {
        val state = gameRepository.currentState()
        return state.quest.activeQuestIds
            .mapNotNull { registry[it] }
            .filter { it.cityId == cityId }
    }

    fun getAllRelevantQuestsForCity(cityId: String, state: GameState?): List<QuestDefinition> {
        val s = state ?: gameRepository.currentState()
        return registry.values.filter { it.cityId == cityId }.filter { def ->
            val stat = getStatus(def.id, s)
            stat == QuestStatus.AVAILABLE || stat == QuestStatus.ACTIVE || stat == QuestStatus.OBJECTIVE_MET
        }
    }

    fun validateQuestGraph(): List<String> {
        val issues = mutableListOf<String>()
        registry.keys.forEach { id ->
            val visited = mutableSetOf<String>()
            if (hasCycle(id, visited, mutableSetOf())) {
                issues.add("Cycle detected involving quest: $id")
            }
        }
        return issues
    }

    private fun hasCycle(id: String, visited: MutableSet<String>, stack: MutableSet<String>): Boolean {
        if (stack.contains(id)) return true
        if (visited.contains(id)) return false
        visited.add(id)
        stack.add(id)
        registry[id]?.prerequisiteQuestId?.let { pre ->
            if (hasCycle(pre, visited, stack)) return true
        }
        stack.remove(id)
        return false
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
    val category: QuestCategory = QuestCategory.MIXED,
    val recommendedLevel: Int = 1
)
