package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue

enum class QuestOriginType {
    ZDARZENIE_MIEJSKIE,
    LOKACJA_PROCEDURALNA,
    LOKACJA_NPC
}

enum class QuestStatus {
    DOSTEPNE,
    AKTYWNE,
    UKONCZONE,
    PRZERWANE
}

data class QuestEntry(
    val id: String,
    val title: String,
    val description: String,
    val cityId: String,
    val originType: QuestOriginType,
    val originRefId: String,
    val rewardGold: Int,
    val status: QuestStatus = QuestStatus.DOSTEPNE,
    val requiredQuestIds: List<String> = emptyList(),
    val objective: String = "Brak szczegółowych wytycznych.",
    val stabilityImpact: Float = 0.02f,
    val collapseSlowdown: Float = 0.01f
)

object QuestSystem {
    private val quests = linkedMapOf<String, QuestEntry>()
    private var currentSeed: Int = 0

    fun clear() {
        quests.clear()
        currentSeed = 0
    }

    fun seedIntegratedContent(seed: Int = 1) {
        // ALWAYS SEED IF EMPTY
        if (quests.isNotEmpty() && currentSeed == seed) return

        clear()
        currentSeed = seed

        // 1. STARTING QUEST (MANDATORY)
        register(QuestEntry(
            id = "q_start_01",
            title = "Pustka na Wybrzeżu",
            description = "Aelion czeka na kogoś, kto potrafi słuchać mgły.",
            cityId = "wybrzeze_polnocne",
            originType = QuestOriginType.LOKACJA_NPC,
            originRefId = "aelion",
            rewardGold = 50,
            objective = "Porozmawiaj z Aelionem."
        ))

        // 2. TEMPLATE QUESTS
        val rand = kotlin.random.Random(seed)
        val cities = CityCatalogue.all()
        
        QuestRegistry.allTemplates.take(15).forEach { t ->
            register(QuestEntry(
                id = t.id,
                title = t.title,
                description = t.description,
                cityId = t.preferredCityId ?: cities.random(rand).id,
                originType = QuestOriginType.LOKACJA_PROCEDURALNA,
                originRefId = t.category,
                rewardGold = t.baseReward,
                objective = t.objective
            ))
        }

        // FORCE PERSISTENCE RESTORE
        val state = GameRepository.state
        state.quest.activeQuests.forEach { id ->
            quests[id]?.let { quests[id] = it.copy(status = QuestStatus.AKTYWNE) }
        }
        state.quest.completedQuests.forEach { id ->
            quests[id]?.let { quests[id] = it.copy(status = QuestStatus.UKONCZONE) }
        }
    }

    fun register(entry: QuestEntry) {
        quests[entry.id] = entry
    }

    fun all(): List<QuestEntry> = quests.values.toList()

    fun getQuest(id: String): QuestEntry? = quests[id]

    fun availableForCity(cityId: String): List<QuestEntry> {
        val normalized = cityId.lowercase().replace(" ", "_")
        return quests.values.filter { it.cityId == normalized && it.status == QuestStatus.DOSTEPNE }
    }

    fun activate(questId: String) {
        quests[questId]?.let {
            quests[questId] = it.copy(status = QuestStatus.AKTYWNE)
            if (!GameRepository.state.quest.activeQuests.contains(questId)) {
                GameRepository.state.quest.activeQuests.add(questId)
            }
        }
    }

    fun complete(questId: String) {
        quests[questId]?.let {
            quests[questId] = it.copy(status = QuestStatus.UKONCZONE)
            GameRepository.state.quest.activeQuests.remove(questId)
            if (!GameRepository.state.quest.completedQuests.contains(questId)) {
                GameRepository.state.quest.completedQuests.add(questId)
            }
            GameRepository.state.gold += it.rewardGold
        }
    }
}
