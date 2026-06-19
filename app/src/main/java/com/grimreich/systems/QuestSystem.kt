package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import javax.inject.Inject
import javax.inject.Singleton

enum class QuestStatus { DOSTEPNE, AKTYWNE, UKONCZONE, ODRZUCONE }

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
    private val gameRepository: GameRepository,
    private val cityCatalogue: CityCatalogue
) {
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

    fun availableForCity(cityId: String): List<QuestEntry> {
        return allQuests.values.filter { 
            it.cityId == cityId && it.status == QuestStatus.DOSTEPNE 
        }
    }

    fun clear() {
        allQuests.clear()
    }

    fun seedIntegratedContent(seed: Int = 1) {
        if (allQuests.isNotEmpty()) return
        
        register(QuestEntry(
            id = "q_start_01",
            title = "Początek Końca",
            description = "Znajdź Aeliona na Wybrzeżu Północnym.",
            objective = "Porozmawiaj z Aelionem",
            cityId = "wybrzeze_polnocne",
            rewardGold = 50,
            originRefId = "aelion"
        ))
    }
}
