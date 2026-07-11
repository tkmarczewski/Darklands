package com.grimreich.systems

import com.grimreich.core.*
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

    fun getAllDefinitions(): Collection<QuestDefinition> = registry.values

    fun getStatus(questId: String, state: GameState? = null, visited: MutableSet<String> = mutableSetOf()): QuestStatus {
        val actualState = state ?: gameRepository.currentState()

        if (questId.startsWith("resurrect_")) {
            val hId = questId.removePrefix("resurrect_")
            val hero = actualState.party.find { it.id == hId }
            if (hero == null || !hero.isDead) return QuestStatus.LOCKED
            
            val corpseId = "corpse_${hero.id}"
            val hasCorpse = actualState.inventory.any { it.instanceId == corpseId }
            return if (hasCorpse) QuestStatus.AVAILABLE else QuestStatus.LOCKED
        }

        val def = registry[questId] ?: return QuestStatus.LOCKED
        if (!visited.add(questId)) return QuestStatus.LOCKED

        if (actualState.quest.completedQuestIds.contains(questId)) {
            return if (def.repeatable) QuestStatus.AVAILABLE else QuestStatus.COMPLETED
        }
        
        if (actualState.quest.activeQuestIds.contains(questId)) {
            val progress = actualState.quest.progress[questId]
            return progress?.status ?: QuestStatus.ACTIVE
        }

        if (actualState.world.day < def.minWorldDay) return QuestStatus.LOCKED
        if (actualState.metaAwarenessLevel < def.requiredMetaAwareness) return QuestStatus.LOCKED

        if (def.prerequisiteQuestId != null) {
            val preStatus = getStatus(def.prerequisiteQuestId, actualState, visited)
            if (preStatus != QuestStatus.COMPLETED) return QuestStatus.LOCKED
        }

        return QuestStatus.AVAILABLE
    }

    fun activateQuest(questId: String) = activateQuestDirect(gameRepository.currentState(), questId)

    fun activateQuestDirect(state: GameState, questId: String) {
        if (getStatus(questId, state) != QuestStatus.AVAILABLE) return
        
        state.quest.activeQuestIds.add(questId)
        state.quest.progress[questId] = QuestProgress(questId = questId, status = QuestStatus.ACTIVE)
    }

    fun advanceStep(questId: String) = advanceStepDirect(gameRepository.currentState(), questId)

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

    fun completeQuest(questId: String) = completeQuestDirect(gameRepository.currentState(), questId)

    fun completeQuestDirect(state: GameState, questId: String) {
        if (state.quest.completedQuestIds.contains(questId)) return
        val p = state.quest.progress[questId] ?: return
        if (p.status != QuestStatus.OBJECTIVE_MET) return
        
        val def = registry[questId] ?: return
        state.quest.activeQuestIds.remove(questId)
        state.quest.completedQuestIds.add(questId)
        state.quest.progress[questId] = p.copy(status = QuestStatus.COMPLETED)
        state.gold += def.rewardGold
        state.gold += def.rewardGold
        experienceSystem.addPartyXpDirect(state, def.recommendedLevel * 50)
        
        state.logEntries.add("ZADANIE UKOŃCZONE: ${def.title}. Nagroda: ${def.rewardGold} zł.")
    }

    fun failQuestDirect(state: GameState, questId: String) {
        state.quest.activeQuestIds.remove(questId)
        state.quest.progress[questId]?.let {
            state.quest.progress[questId] = it.copy(status = QuestStatus.FAILED)
        }
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

    fun isObjectiveMet(questId: String, state: GameState? = null): Boolean {
        val s = state ?: gameRepository.currentState()
        return s.quest.progress[questId]?.status == QuestStatus.OBJECTIVE_MET
    }

    fun getActiveQuestsForCity(cityId: String): List<QuestDefinition> {
        val state = gameRepository.currentState()
        return state.quest.activeQuestIds
            .mapNotNull { registry[it] }
            .filter { it.cityId == cityId || it.steps.getOrNull(state.quest.progress[it.id]?.currentStepIndex ?: -1)?.targetId == cityId }
    }

    fun getVisibleQuestBoard(state: GameState): Map<String, List<QuestDefinition>> {
        return registry.values
            .filter { !it.isHidden && getStatus(it.id, state) == QuestStatus.AVAILABLE }
            .groupBy { it.cityId }
            .mapValues { (cityId, quests) ->
                val seed = cityId.hashCode().toLong() + (state.world.day / 3)
                val cityRandom = kotlin.random.Random(seed)
                quests.shuffled(cityRandom).take(3) // Max 3 quests per city
            }
    }
    
    fun getAvailableQuestsForCity(cityId: String, state: GameState): List<QuestDefinition> {
        val allAvailable = registry.values.filter {
            it.cityId == cityId && !it.isHidden && getStatus(it.id, state) == QuestStatus.AVAILABLE
        }
        
        val seed = cityId.hashCode().toLong() + (state.world.day / 3)
        val cityRandom = kotlin.random.Random(seed)
        
        return allAvailable.shuffled(cityRandom).take(3)
            .sortedWith(compareBy<QuestDefinition> { it.chainId ?: "zzz" }.thenBy { it.chainOrder }.thenBy { it.recommendedLevel })
    }
}

enum class StepType { COMBAT, DIALOGUE, INVESTIGATION, SOCIAL, META, EXPEDITION }
enum class QuestCategory { COMBAT, SOCIAL, INVESTIGATION, MIXED, META, ANOMALY, DRAMA, BEAST, INTRIGUE }

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
    val recommendedLevel: Int = 1,
    val chainId: String? = null,
    val chainOrder: Int = 0,
    val minWorldDay: Int = 1,
    val requiredMetaAwareness: Int = 0,
    val repeatable: Boolean = false,
    val isHidden: Boolean = false
)
