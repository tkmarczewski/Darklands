package com.grimreich.systems

import com.grimreich.core.GameRepository
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

sealed class ExpeditionResult {
    data class StartCombat(
        val enemyId: String,
        val enemyName: String,
        val enemyHp: Int,
        val enemyAtk: Int,
        val enemyDef: Int
    ) : ExpeditionResult()

    data class StartDialogue(val npcId: String) : ExpeditionResult()
    data class StartInvestigation(val locationId: String) : ExpeditionResult()
    data class Travel(val locationId: String) : ExpeditionResult()
    data class OpenChoice(val choiceId: String, val description: String) : ExpeditionResult()
    data class QuestCompleted(val questId: String, val reward: Int) : ExpeditionResult()
    data class Error(val message: String) : ExpeditionResult()
}

@Singleton
class ExpeditionManager @Inject constructor(
    private val questSystemProvider: Lazy<QuestSystem>,
    private val gameRepositoryProvider: Lazy<GameRepository>
) {
    private val questSystem get() = questSystemProvider.get()
    private val gameRepository get() = gameRepositoryProvider.get()

    private val stepIndices = mutableMapOf<String, Int>()

    fun startQuest(questId: String): ExpeditionResult {
        val definition = QuestDefinitionRegistry.getById(questId) 
            ?: createDefaultDefinition(questId)
            ?: return ExpeditionResult.Error("Brak definicji questa: $questId")

        stepIndices[questId] = 0
        return resolveStep(questId, definition)
    }

    private fun createDefaultDefinition(questId: String): QuestDefinition? {
        val entry = questSystem.getQuest(questId) ?: return null
        return QuestDefinition(
            id = entry.id,
            title = entry.title,
            description = entry.description,
            category = "Standard",
            baseReward = entry.rewardGold,
            steps = listOf(
                QuestStep(
                    id = "${entry.id}_step",
                    type = if (entry.hasCombat) QuestStepType.COMBAT else QuestStepType.INVESTIGATION,
                    targetId = entry.id,
                    description = entry.objective
                )
            )
        )
    }

    fun currentStep(questId: String): QuestStep? {
        val definition = QuestDefinitionRegistry.getById(questId) ?: return null
        val index = stepIndices[questId] ?: 0
        return definition.steps.getOrNull(index)
    }

    fun onStepFinished(questId: String, success: Boolean): ExpeditionResult {
        if (!success) return ExpeditionResult.Error("Krok nieudany")

        val definition = QuestDefinitionRegistry.getById(questId)
            ?: createDefaultDefinition(questId)
            ?: return ExpeditionResult.Error("Brak definicji questa: $questId")

        val currentIndex = stepIndices[questId] ?: 0
        val nextIndex = currentIndex + 1

        return if (nextIndex >= definition.steps.size) {
            stepIndices.remove(questId)
            questSystem.markObjectiveComplete(questId)
            ExpeditionResult.QuestCompleted(questId, definition.baseReward)
        } else {
            stepIndices[questId] = nextIndex
            resolveStep(questId, definition)
        }
    }

    private fun resolveStep(questId: String, definition: QuestDefinition): ExpeditionResult {
        val index = stepIndices[questId] ?: 0
        val step = definition.steps.getOrNull(index)
            ?: return ExpeditionResult.Error("Brak kroku")

        return when (step.type) {
            QuestStepType.COMBAT -> {
                val stats = QuestRegistry.allTemplates.find { it.id == questId }?.enemyStats
                    ?: QuestRegistry.bloodChain.stages.find { it.id == questId }?.enemyStats
                    ?: QuestRegistry.verdictChain.stages.find { it.id == questId }?.enemyStats
                
                ExpeditionResult.StartCombat(
                    enemyId = step.targetId,
                    enemyName = stats?.name ?: "Potworna Istota",
                    enemyHp = stats?.hp ?: 50,
                    enemyAtk = stats?.atk ?: 10,
                    enemyDef = stats?.def ?: 5
                )
            }
            QuestStepType.DIALOGUE -> ExpeditionResult.StartDialogue(step.targetId)
            QuestStepType.INVESTIGATION -> ExpeditionResult.StartInvestigation(step.targetId)
            QuestStepType.TRAVEL -> ExpeditionResult.Travel(step.targetId)
            QuestStepType.CHOICE -> ExpeditionResult.OpenChoice(step.targetId, step.description)
        }
    }

    fun getStepInfo(questId: String): String {
        val definition = QuestDefinitionRegistry.getById(questId) ?: createDefaultDefinition(questId) ?: return "Brak"
        val index = stepIndices[questId] ?: 0
        return "[${index + 1}/${definition.steps.size}]"
    }

    fun resetProgress(questId: String) {
        stepIndices.remove(questId)
    }
}
