package com.grimreich.systems

import com.grimreich.core.GameRepository
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
        
        register(
            QuestEntry(
                id = "q_start_02",
                title = "Szept w Ciemności",
                description = "W Twierdzy Żelaznej słyszano głosy dochodzące z zamarzniętych studni.",
                objective = "Zbadaj studnie w Twierdzy",
                cityId = "twierdza_zelazna",
                rewardGold = 75,
                originRefId = "guard",
            )
        )

        register(
            QuestEntry(
                id = "q_start_03",
                title = "Ostatnia Wieczerza",
                description = "W Porcie Mglistym brakuje zapasów. Ktoś kradnie ryby prosto z sieci.",
                objective = "Złap złodzieja w Porcie",
                cityId = "port_mglisty",
                rewardGold = 40,
                originRefId = "merchant",
            )
        )

        register(
            QuestEntry(
                id = "q_rand_01",
                title = "Milcząca Modlitwa",
                description = "Mnisi z Opactwa Ciszy szukają kogoś, kto odzyska skradziony dzwon.",
                objective = "Odzyskaj dzwon Opactwa",
                cityId = "opactwo_ciszy",
                rewardGold = 60,
                originRefId = "mystic",
            )
        )

        register(
            QuestEntry(
                id = "q_rand_02",
                title = "Cień Przeszłości",
                description = "Ktoś widział statek widmo dryfujący u wybrzeży.",
                objective = "Zbadaj wrak na Wybrzeżu",
                cityId = "wybrzeze_polnocne",
                rewardGold = 55,
                originRefId = "aelion",
            )
        )

        register(
            QuestEntry(
                id = "q_rand_03",
                title = "Zatruta Mgła",
                description = "Gęsta, nienaturalna mgła dusi mieszkańców Portu.",
                objective = "Oczyść opary w Porcie",
                cityId = "port_mglisty",
                rewardGold = 90,
                originRefId = "zealot",
            )
        )

        register(
            QuestEntry(
                id = "q_rand_04",
                title = "Zamarznięta Groza",
                description = "Potwór z lodu terroryzuje bramy Twierdzy.",
                objective = "Zgładź lodową bestię",
                cityId = "twierdza_zelazna",
                rewardGold = 45,
                originRefId = "guard",
            )
        )
    }
}
