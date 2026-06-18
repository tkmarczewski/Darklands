package com.grimreich.systems

import com.grimreich.core.GameRepository

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
    val objective: String = "Brak szczegółowych wytycznych."
)

object QuestSystem {
    private val quests = mutableMapOf<String, QuestEntry>()

    fun clear() {
        quests.clear()
    }

    private fun normalize(id: String): String {
        return id.lowercase()
            .replace("ą", "a").replace("ć", "c").replace("ę", "e")
            .replace("ł", "l").replace("ń", "n").replace("ó", "o")
            .replace("ś", "s").replace("ź", "z").replace("ż", "z")
            .replace(" ", "_")
    }

    fun seedIntegratedContent(seed: Int = 1) {
        clear()
        
        // 1. STARTING QUEST - Force register for normalized starting city
        register(QuestEntry(
            id = "q_start_01",
            title = "Cisza Przed Burzą",
            description = "Aelion czeka na kogoś, kto potrafi słuchać mgły.",
            cityId = "wybrzeze_polnocne",
            originType = QuestOriginType.LOKACJA_NPC,
            originRefId = "aelion",
            rewardGold = 50,
            objective = "Porozmawiaj z Aelionem."
        ))

        // 2. Add variety from Registry with normalized assigned cities
        QuestRegistry.allTemplates.forEach { t ->
            val rawCity = t.preferredCityId ?: "wybrzeze_polnocne"
            register(QuestEntry(
                id = t.id,
                title = t.title,
                description = t.description,
                cityId = normalize(rawCity),
                originType = QuestOriginType.LOKACJA_PROCEDURALNA,
                originRefId = t.category,
                rewardGold = t.baseReward,
                objective = t.objective
            ))
        }

        // SYNC WITH PERSISTENT STATE
        val state = GameRepository.state
        state.quest.activeQuests.forEach { id ->
            quests[id] = quests[id]?.copy(status = QuestStatus.AKTYWNE) ?: return@forEach
        }
        state.quest.completedQuests.forEach { id ->
            quests[id] = quests[id]?.copy(status = QuestStatus.UKONCZONE) ?: return@forEach
        }
    }

    fun register(entry: QuestEntry) {
        quests[entry.id] = entry
    }

    fun all(): List<QuestEntry> = quests.values.toList()

    fun getQuest(id: String): QuestEntry? = quests[id]

    fun availableForCity(cityId: String): List<QuestEntry> {
        val target = normalize(cityId)
        return quests.values.filter { it.cityId == target && it.status == QuestStatus.DOSTEPNE }
    }

    fun activate(questId: String): QuestEntry {
        val quest = quests[questId] ?: error("Unknown quest: $questId")
        val updated = quest.copy(status = QuestStatus.AKTYWNE)
        quests[questId] = updated
        if (!GameRepository.state.quest.activeQuests.contains(questId)) {
            GameRepository.state.quest.activeQuests.add(questId)
        }
        return updated
    }

    fun complete(questId: String): QuestEntry {
        val quest = quests[questId] ?: error("Unknown quest: $questId")
        val updated = quest.copy(status = QuestStatus.UKONCZONE)
        quests[questId] = updated
        GameRepository.state.quest.activeQuests.remove(questId)
        if (!GameRepository.state.quest.completedQuests.contains(questId)) {
            GameRepository.state.quest.completedQuests.add(questId)
        }
        GameRepository.state.gold += updated.rewardGold
        return updated
    }
}
