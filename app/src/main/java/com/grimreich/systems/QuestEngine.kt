package com.grimreich.systems

import android.content.Context
import com.grimreich.R
import com.grimreich.core.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestEngine @Inject constructor(
    @param:ApplicationContext private val context: Context,
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

        // PURIFICATION: All ID checks must be lowercase
        val normalizedId = questId.lowercase().trim()

        if (normalizedId.startsWith("resurrect_")) {
            val hId = normalizedId.removePrefix("resurrect_")
            val hero = actualState.party.find { it.id == hId }
            
            if (hero != null && hero.isDead) {
                val corpseId = "corpse_${hero.id}"
                val hasCorpse = actualState.inventory.any { it.instanceId == corpseId }
                status = if (hasCorpse) QuestStatus.AVAILABLE else QuestStatus.LOCKED
            } else {
                status = QuestStatus.LOCKED
            }
        } else {
            val def = registry[normalizedId]
            if (def != null && visited.add(normalizedId)) {
                status = evaluateDefinitionStatus(def, actualState, visited)
            }
        }

        return status
    }

    private fun evaluateDefinitionStatus(def: QuestDefinition, state: GameState, visited: MutableSet<String>): QuestStatus {
        // --- PRIORITY 1: FINAL STATES ---
        if (state.quest.completedQuestIds.contains(def.id)) return QuestStatus.COMPLETED
        if (state.quest.failedQuestIds.contains(def.id)) return QuestStatus.FAILED

        // --- PRIORITY 2: CURRENTLY ACTIVE ---
        if (state.quest.activeQuestIds.contains(def.id)) {
            return state.quest.progress[def.id]?.status ?: QuestStatus.ACTIVE
        }

        // --- PRIORITY 3: LOGIC REQUIREMENTS ---
        if (state.world.day < def.minWorldDay) return QuestStatus.LOCKED
        if (state.metaAwarenessLevel < def.requiredMetaAwareness) return QuestStatus.LOCKED

        // Prerequisites
        if (def.prerequisiteQuestId != null) {
            val preStatus = getStatus(def.prerequisiteQuestId, state, visited)
            if (preStatus != QuestStatus.COMPLETED) return QuestStatus.LOCKED
        }

        return QuestStatus.AVAILABLE
    }

    fun activateQuestDirect(state: GameState, questId: String) {
        val normalizedId = questId.lowercase().trim()
        val currentStatus = getStatus(normalizedId, state)
        if (currentStatus != QuestStatus.AVAILABLE) {
            android.util.Log.d("QuestEngine", "Activation blocked: $normalizedId is $currentStatus")
            return
        }
        
        state.quest.activeQuestIds.add(normalizedId)
        state.quest.progress[normalizedId] = QuestProgress(questId = normalizedId, status = QuestStatus.ACTIVE)
        state.logEntries.add("۞ NOWE ZADANIE: ${getDefinition(normalizedId)?.title ?: normalizedId}")
    }

    fun advanceStepDirect(state: GameState, questId: String) {
        val normalizedId = questId.lowercase().trim()
        val p = state.quest.progress[normalizedId] ?: return
        if (p.status != QuestStatus.ACTIVE) return
        val def = registry[normalizedId] ?: return
        
        if (p.currentStepIndex < def.steps.size - 1) {
            state.quest.progress[normalizedId] = p.copy(currentStepIndex = p.currentStepIndex + 1)
            android.util.Log.d("QuestEngine", "Advanced $normalizedId to step ${p.currentStepIndex + 1}")
        } else {
            state.quest.progress[normalizedId] = p.copy(status = QuestStatus.OBJECTIVE_MET)
            state.logEntries.add("۞ CEL OSIĄGNIĘTY: ${def.title}")
            state.logEntries.add("> ${context.getString(R.string.quest_return_to, def.originNpcId.uppercase())}")
        }
    }

    fun completeQuestDirect(state: GameState, questId: String) {
        val normalizedId = questId.lowercase().trim()
        // Resolve "active" alias from dialogue context
        val action = state.pendingAction
        val actualQuestId = if (normalizedId == "active" && action is com.grimreich.core.PendingWorldAction.Dialogue && action.relatedQuestId != null) {
            action.relatedQuestId!!.lowercase()
        } else normalizedId

        if (state.quest.completedQuestIds.contains(actualQuestId)) {
            state.quest.activeQuestIds.remove(actualQuestId)
            return
        }

        val p = state.quest.progress[actualQuestId] ?: return
        
        // --- IRONCLAD VALIDATION ---
        val def = registry[actualQuestId] ?: return
        val currentCityId = state.world.locationId.lowercase()
        
        if (def.cityId.lowercase() != currentCityId) {
            android.util.Log.e("QuestEngine", "Attempted turn-in in wrong city: $currentCityId (expected ${def.cityId})")
            return
        }
        
        if (action is com.grimreich.core.PendingWorldAction.Dialogue) {
             val normalizedRole = action.npcRole.lowercase()
             val normalizedOrigin = def.originNpcId.lowercase()
             if (normalizedOrigin != "none" && normalizedRole != normalizedOrigin && action.npcName.lowercase() != normalizedOrigin) {
                 android.util.Log.e("QuestEngine", "Attempted turn-in to wrong NPC: $normalizedRole (expected $normalizedOrigin)")
                 return
             }
        }

        if (p.status != QuestStatus.OBJECTIVE_MET && p.status != QuestStatus.ACTIVE) return
        
        // --- ATOMIC TRANSITION ---
        state.quest.activeQuestIds.remove(actualQuestId)
        state.quest.completedQuestIds.add(actualQuestId)
        state.quest.progress[actualQuestId] = p.copy(status = QuestStatus.COMPLETED)
        
        state.gold += def.rewardGold
        experienceSystem.addPartyXpDirect(state, def.recommendedLevel * 50).forEach { state.logEntries.add(it) }
        
        state.logEntries.add("۞ ZADANIE UKOŃCZONE: ${def.title}")
        android.util.Log.i("QuestEngine", "Quest $actualQuestId COMPLETED and removed from active list.")
    }

    fun failQuestDirect(state: GameState, questId: String) {
        val normalizedId = questId.lowercase().trim()
        state.quest.activeQuestIds.remove(normalizedId)
        state.quest.failedQuestIds.add(normalizedId)
        state.quest.progress[normalizedId] = state.quest.progress[normalizedId]?.copy(status = QuestStatus.FAILED) ?: QuestProgress(normalizedId, QuestStatus.FAILED)
    }

    fun getCurrentObjective(questId: String, state: GameState? = null): String {
        val normalizedId = questId.lowercase().trim()
        val def = registry[normalizedId] ?: return "???"
        val actualState = state ?: gameRepository.currentState()
        val p = actualState.quest.progress[normalizedId] ?: return def.description
        
        return if (p.status == QuestStatus.OBJECTIVE_MET) {
            context.getString(R.string.quest_return_to, def.originNpcId.uppercase())
        } else {
            def.steps.getOrNull(p.currentStepIndex)?.description ?: def.description
        }
    }

    fun isObjectiveMet(questId: String, state: GameState? = null): Boolean {
        val normalizedId = questId.lowercase().trim()
        val s = state ?: gameRepository.currentState()
        return s.quest.progress[normalizedId]?.status == QuestStatus.OBJECTIVE_MET
    }

    fun getActiveQuestsForCity(cityId: String): List<QuestDefinition> {
        val state = gameRepository.currentState()
        return state.quest.activeQuestIds
            .mapNotNull { registry[it] }
            .filter { it.cityId.lowercase() == cityId.lowercase() }
    }

    fun getVisibleQuestBoard(state: GameState): Map<String, List<QuestDefinition>> {
        val sharedVisited = mutableSetOf<String>()
        return registry.values
            .filter { !it.isHidden && getStatus(it.id, state, sharedVisited) == QuestStatus.AVAILABLE }
            .groupBy { it.cityId.lowercase() }
            .mapValues { (cityId, quests) ->
                shuffleQuests(quests, cityId, state.world.day)
            }
    }
    
    fun getAvailableQuestsForCity(cityId: String, state: GameState): List<QuestDefinition> {
        val sharedVisited = mutableSetOf<String>()
        val cityLower = cityId.lowercase()
        val allAvailable = registry.values.filter {
            it.cityId.lowercase() == cityLower && !it.isHidden && getStatus(it.id, state, sharedVisited) == QuestStatus.AVAILABLE
        }
        
        val priorityQuests = allAvailable.filter { it.chainId != null }
        val randomQuests = allAvailable.filter { it.chainId == null }
        
        return shuffleQuests(priorityQuests + randomQuests, cityLower, state.world.day)
            .sortedWith(compareBy<QuestDefinition> { it.chainId ?: "zzz" }.thenBy { it.chainOrder }.thenBy { it.recommendedLevel })
    }

    private fun shuffleQuests(quests: List<QuestDefinition>, cityId: String, day: Int): List<QuestDefinition> {
        val seed = cityId.hashCode().toLong() + (day / 3)
        val cityRandom = kotlin.random.Random(seed)
        val sorted = quests.sortedByDescending { it.chainId != null }
        return sorted.shuffled(cityRandom).take(6)
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
    val minWorldDay: Int = 0,
    val requiredMetaAwareness: Int = 0,
    val repeatable: Boolean = false,
    val isHidden: Boolean = false
)
