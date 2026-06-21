package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestRegistry
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

enum class QuestStatus { DOSTEPNE, AKTYWNE, UKONCZONE }

data class QuestEntry(
    val id: String,
    val title: String,
    val description: String,
    val objective: String,
    val cityId: String,
    val rewardGold: Int,
    var status: QuestStatus = QuestStatus.DOSTEPNE,
    val originRefId: String = ""
)

@Singleton
class QuestSystem @Inject constructor(
    private val gameRepositoryProvider: Lazy<GameRepository>
) {
    private val gameRepository get() = gameRepositoryProvider.get()
    private val allQuests = mutableMapOf<String, QuestEntry>()

    fun register(quest: QuestEntry) {
        allQuests[quest.id] = quest
    }

    fun getQuest(id: String): QuestEntry? = allQuests[id]

    fun all(): List<QuestEntry> = allQuests.values.toList()

    fun activate(questId: String): QuestEntry {
        val quest = allQuests[questId] ?: error("Nie znaleziono zadania: $questId")
        quest.status = QuestStatus.AKTYWNE
        val state = gameRepository.currentState()
        if (!state.quest.activeQuests.contains(questId)) {
            state.quest.activeQuests.add(questId)
        }
        gameRepository.persistCurrentState()
        return quest
    }

    fun complete(questId: String): QuestEntry {
        val quest = allQuests[questId] ?: error("Nie znaleziono zadania: $questId")
        quest.status = QuestStatus.UKONCZONE
        val state = gameRepository.currentState()
        state.quest.activeQuests.remove(questId)
        if (!state.quest.completedQuests.contains(questId)) {
            state.quest.completedQuests.add(questId)
        }
        state.gold += quest.rewardGold
        gameRepository.persistCurrentState()
        return quest
    }

    fun availableForCity(cityId: String, excludeIds: Set<String> = emptySet()): List<QuestEntry> {
        return allQuests.values.filter { 
            (it.cityId == cityId) && (it.status == QuestStatus.DOSTEPNE) && !excludeIds.contains(it.id)
        }
    }

    fun clear() {
        allQuests.clear()
    }

    fun seedIntegratedContent() {
        if (allQuests.isNotEmpty()) return
        
        // Seed all templates from QuestRegistry
        QuestRegistry.allTemplates.forEach { template ->
            register(
                QuestEntry(
                    id = template.id,
                    title = template.title,
                    description = template.description,
                    objective = template.objective,
                    cityId = template.preferredCityId ?: "wybrzeze_polnocne",
                    rewardGold = template.baseReward,
                    originRefId = when (template.category) {
                        "Intrigue" -> "merchant"
                        "Anomaly" -> "mystic"
                        "Beast" -> "guard"
                        "Drama" -> "zealot"
                        else -> "mystic"
                    }
                )
            )
        }

        // Seed blood chain
        QuestRegistry.bloodChain.stages.forEach { template ->
             register(
                QuestEntry(
                    id = template.id,
                    title = template.title,
                    description = template.description,
                    objective = template.objective,
                    cityId = "wybrzeze_polnocne", // Chain starts here
                    rewardGold = template.baseReward,
                    originRefId = "mystic"
                )
            )
        }
    }
}
