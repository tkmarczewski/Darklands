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
        if (registry.containsKey(definition.id)) {
            android.util.Log.w("QuestEngine", "Duplicate quest ID registered: ${definition.id}. Overwriting.")
        }
        registry[definition.id] = definition
    }

    fun clearRegistry() {
        registry.clear()
    }

    fun getDefinition(id: String) = registry[id]

    fun getAllDefinitions(): Collection<QuestDefinition> = registry.values

    fun getStatus(questId: String, state: GameState? = null, visited: MutableSet<String> = mutableSetOf()): QuestStatus {
        val actualState = state ?: gameRepository.currentState()
        var status = QuestStatus.LOCKED

        if (questId.startsWith("resurrect_")) {
            val hId = questId.removePrefix("resurrect_")
            val hero = actualState.party.find { it.id == hId }
            if (hero != null && hero.isDead) {
                val corpseId = "corpse_${hero.id}"
                val hasCorpse = actualState.inventory.any { it.instanceId == corpseId }
                status = if (hasCorpse) QuestStatus.AVAILABLE else QuestStatus.LOCKED
            }
        } else {
            val def = registry[questId]
            if (def != null && visited.add(questId)) {
                status = evaluateDefinitionStatus(def, actualState, visited)
            }
        }

        return status
    }

    private fun evaluateDefinitionStatus(def: QuestDefinition, state: GameState, visited: MutableSet<String>): QuestStatus {
        // --- PRIORITY 1: FINAL STATES ---
        if (state.quest.completedQuestIds.contains(def.id)) {
            return QuestStatus.COMPLETED
        }
        
        if (state.quest.failedQuestIds.contains(def.id)) {
            return QuestStatus.FAILED
        }

        // --- PRIORITY 2: CURRENTLY ACTIVE ---
        // Rygorystyczne sprawdzenie: jeśli jest w activeQuestIds, to NIE MOŻE być AVAILABLE
        if (state.quest.activeQuestIds.contains(def.id)) {
            val progress = state.quest.progress[def.id]
            return progress?.status ?: QuestStatus.ACTIVE
        }

        // Logic Requirements
        val dayOk = state.world.day >= def.minWorldDay
        val metaOk = state.metaAwarenessLevel >= def.requiredMetaAwareness
        
        if (!dayOk || !metaOk) return QuestStatus.LOCKED

        // Special Flags
        if (def.id == "q_verdict_1" && !state.quest.worldFlags.contains("verdict_campaign_ready")) {
            return QuestStatus.LOCKED
        }

        // Prerequisites
        if (def.prerequisiteQuestId != null) {
            val preStatus = getStatus(def.prerequisiteQuestId, state, visited)
            if (preStatus != QuestStatus.COMPLETED) return QuestStatus.LOCKED
        }

        return QuestStatus.AVAILABLE
    }

    fun activateQuest(questId: String) = activateQuestDirect(gameRepository.currentState(), questId)

    fun activateQuestDirect(state: GameState, questId: String) {
        val currentStatus = getStatus(questId, state)
        if (currentStatus != QuestStatus.AVAILABLE) {
            android.util.Log.d("QuestEngine", "Blokada aktywacji: Quest $questId ma status $currentStatus")
            return
        }
        
        state.quest.activeQuestIds.add(questId)
        state.quest.progress[questId] = QuestProgress(questId = questId, status = QuestStatus.ACTIVE)
        state.logEntries.add("۞ NOWE ZADANIE: ${getDefinition(questId)?.title ?: questId}")
    }

    fun advanceStep(questId: String) = advanceStepDirect(gameRepository.currentState(), questId)

    fun advanceStepDirect(state: GameState, questId: String) {
        val p = state.quest.progress[questId] ?: return
        if (p.status != QuestStatus.ACTIVE) return
        val def = registry[questId] ?: return
        
        if (def.steps.isEmpty()) {
            state.quest.progress[questId] = p.copy(status = QuestStatus.OBJECTIVE_MET)
            state.logEntries.add("CEL OSIĄGNIĘTY: ${def.title}")
            return
        }

        if (p.currentStepIndex < def.steps.size - 1) {
            state.quest.progress[questId] = p.copy(currentStepIndex = p.currentStepIndex + 1)
        } else {
            state.quest.progress[questId] = p.copy(status = QuestStatus.OBJECTIVE_MET)
            state.logEntries.add("۞ CEL OSIĄGNIĘTY: ${def.title}")
            state.logEntries.add("> Wróć do zleceniodawcy: ${def.originNpcId.uppercase()}")
        }
    }

    fun completeQuest(questId: String) = completeQuestDirect(gameRepository.currentState(), questId)

    fun completeQuestDirect(state: GameState, questId: String) {
        if (state.quest.completedQuestIds.contains(questId)) return
        
        // relatedQuestId from pendingAction should have priority
        val action = state.pendingAction
        val actualQuestId = if (questId == "ACTIVE" && action is com.grimreich.core.PendingWorldAction.Dialogue && action.relatedQuestId != null) {
            action.relatedQuestId!!
        } else {
            questId
        }

        val p = state.quest.progress[actualQuestId] ?: return
        if (p.status != QuestStatus.OBJECTIVE_MET) return
        
        val def = registry[actualQuestId] ?: return
        state.quest.activeQuestIds.remove(actualQuestId)
        state.quest.completedQuestIds.add(actualQuestId)
        state.quest.progress[actualQuestId] = p.copy(status = QuestStatus.COMPLETED)
        state.gold += def.rewardGold
        val xpMessages = experienceSystem.addPartyXpDirect(state, def.recommendedLevel * 50)
        
        state.logEntries.add("ZADANIE UKOŃCZONE: ${def.title}. Otrzymano nagrodę: ${def.rewardGold} zł.")
        state.logEntries.addAll(xpMessages)
    }

    fun failQuestDirect(state: GameState, questId: String) {
        state.quest.activeQuestIds.remove(questId)
        state.quest.failedQuestIds.add(questId)
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
            .filter { it.cityId == cityId }
    }

    fun getVisibleQuestBoard(state: GameState): Map<String, List<QuestDefinition>> {
        val sharedVisited = mutableSetOf<String>()
        return registry.values
            .filter { !it.isHidden && getStatus(it.id, state, sharedVisited) == QuestStatus.AVAILABLE }
            .groupBy { it.cityId }
            .mapValues { (cityId, quests) ->
                shuffleQuests(quests, cityId, state.world.day)
            }
    }
    
    fun getAvailableQuestsForCity(cityId: String, state: GameState): List<QuestDefinition> {
        val sharedVisited = mutableSetOf<String>()
        val allAvailable = registry.values.filter {
            it.cityId == cityId && !it.isHidden && getStatus(it.id, state, sharedVisited) == QuestStatus.AVAILABLE
        }
        
        return shuffleQuests(allAvailable, cityId, state.world.day)
            .sortedWith(compareBy<QuestDefinition> { it.chainId ?: "zzz" }.thenBy { it.chainOrder }.thenBy { it.recommendedLevel })
    }

    private fun shuffleQuests(quests: List<QuestDefinition>, cityId: String, day: Int): List<QuestDefinition> {
        val seed = cityId.hashCode().toLong() + (day / 3)
        val cityRandom = kotlin.random.Random(seed)
        return quests.shuffled(cityRandom).take(3)
    }
}

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
