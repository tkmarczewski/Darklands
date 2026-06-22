package com.grimreich.systems

import com.grimreich.core.GameRepository
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

enum class QuestStatus {
    DOSTEPNE, AKTYWNE, UKONCZONE
}

data class QuestEntry(
    val id: String,
    val title: String,
    val description: String,
    val objective: String,
    val cityId: String,
    val rewardGold: Int,
    var status: QuestStatus = QuestStatus.DOSTEPNE,
    val originRefId: String = "mystic",
    val isOutsideCity: Boolean = false // NEW
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

    fun activate(id: String): QuestEntry {
        val quest = allQuests[id] ?: throw IllegalArgumentException("No such quest: $id")
        quest.status = QuestStatus.AKTYWNE
        val state = gameRepository.currentState()
        if (!state.quest.activeQuests.contains(id)) {
            state.quest.activeQuests.add(id)
        }
        return quest
    }

    fun complete(id: String): QuestEntry {
        val quest = allQuests[id] ?: throw IllegalArgumentException("No such quest: $id")
        quest.status = QuestStatus.UKONCZONE
        val state = gameRepository.currentState()
        state.quest.activeQuests.remove(id)
        if (!state.quest.completedQuests.contains(id)) {
            state.quest.completedQuests.add(id)
        }
        state.gold += quest.rewardGold
        gameRepository.log("Ukończono zadanie: ${quest.title}. Nagroda: ${quest.rewardGold} zł.")
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
        
        val canonicalCities = listOf(
            "wybrzeze_polnocne", 
            "rowniny_koronne", 
            "twierdza_zakonu", 
            "serce_krainy", 
            "poludniowe_ruiny", 
            "gory_poludniowe", 
            "pogranicze_stepowe", 
            "ziemie_dzikie"
        )

        // Seed all templates from QuestRegistry - distributing them across cities
        QuestRegistry.allTemplates.forEachIndexed { index, template ->
            register(
                QuestEntry(
                    id = template.id,
                    title = template.title,
                    description = template.description,
                    objective = template.objective,
                    cityId = template.preferredCityId ?: canonicalCities[index % canonicalCities.size],
                    rewardGold = template.baseReward,
                    originRefId = when (template.category) {
                        "Intrigue" -> "merchant"
                        "Anomaly" -> "mystic"
                        "Beast" -> "guard"
                        "Drama" -> "zealot"
                        else -> "mystic"
                    },
                    isOutsideCity = (template.category == "Anomaly" || template.category == "Beast")
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
                    originRefId = "mystic",
                    isOutsideCity = true
                )
            )
        }

        // Seed verdict chain
        QuestRegistry.verdictChain.stages.forEach { template ->
            register(
                QuestEntry(
                    id = template.id,
                    title = template.title,
                    description = template.description,
                    objective = template.objective,
                    cityId = "twierdza_zakonu",
                    rewardGold = template.baseReward,
                    originRefId = "guard",
                    isOutsideCity = (template.id != "q_verdict_1") // First stage is usually in city
                )
            )
        }
    }
}
