package com.grimreich.systems

import com.grimreich.core.GameRepository
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

enum class QuestStatus {
    DOSTEPNE, AKTYWNE, CEL_OSIAGNIETY, UKONCZONE
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
    val isOutsideCity: Boolean = false,
    val nextQuestId: String? = null, // For chains
    val hasCombat: Boolean = false, // true if quest involves a battle
    val category: String = "Normal"
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
        gameRepository.updateState { state ->
            if (!state.quest.activeQuests.contains(id)) {
                state.quest.activeQuests.add(id)
            }
        }
        return quest
    }

    fun markObjectiveComplete(id: String) {
        val quest = allQuests[id] ?: return
        if (quest.status == QuestStatus.AKTYWNE) {
            quest.status = QuestStatus.CEL_OSIAGNIETY
            gameRepository.log("Cel osiągnięty: ${quest.title}. Wróć do zleceniodawcy po nagrodę.")
            gameRepository.persistCurrentState()
        }
    }

    fun complete(id: String): QuestEntry {
        val quest = allQuests[id] ?: throw IllegalArgumentException("No such quest: $id")
        // No longer restricted to only CEL_OSIAGNIETY for safety/legacy reasons, but primarily called from dialogue now
        quest.status = QuestStatus.UKONCZONE
        
        gameRepository.updateState { state ->
            state.quest.activeQuests.remove(id)
            if (!state.quest.completedQuests.contains(id)) {
                state.quest.completedQuests.add(id)
            }
            state.gold += quest.rewardGold
            
            // Handle chains: activate next quest
            quest.nextQuestId?.let { nextId ->
                if (!state.quest.activeQuests.contains(nextId) && !state.quest.completedQuests.contains(nextId)) {
                    state.quest.activeQuests.add(nextId)
                    allQuests[nextId]?.status = QuestStatus.AKTYWNE
                    gameRepository.log("Nowy etap zadania: ${allQuests[nextId]?.title}")
                }
            }
        }
        
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
                    isOutsideCity = (template.category == "Anomaly" || template.category == "Beast"),
                    hasCombat = template.enemyStats != null,
                    category = template.category
                )
            )
        }

        // Seed blood chain
        QuestRegistry.bloodChain.stages.forEachIndexed { i, template ->
             register(
                QuestEntry(
                    id = template.id,
                    title = template.title,
                    description = template.description,
                    objective = template.objective,
                    cityId = "wybrzeze_polnocne",
                    rewardGold = template.baseReward,
                    status = if (i == 0) QuestStatus.DOSTEPNE else QuestStatus.AKTYWNE, // Only first is available initially
                    originRefId = "mystic",
                    hasCombat = template.enemyStats != null,
                    isOutsideCity = true,
                    category = "Chain",
                    nextQuestId = if (i < QuestRegistry.bloodChain.stages.size - 1) QuestRegistry.bloodChain.stages[i+1].id else null
                )
            )
        }

        // Seed verdict chain
        QuestRegistry.verdictChain.stages.forEachIndexed { i, template ->
            register(
                QuestEntry(
                    id = template.id,
                    title = template.title,
                    description = template.description,
                    objective = template.objective,
                    cityId = "twierdza_zakonu",
                    rewardGold = template.baseReward,
                    originRefId = "guard",
                    hasCombat = template.enemyStats != null,
                    isOutsideCity = (template.id != "q_verdict_1"),
                    category = "Verdict",
                    nextQuestId = if (i < QuestRegistry.verdictChain.stages.size - 1) QuestRegistry.verdictChain.stages[i+1].id else null
                )
            )
        }
    }
}
