package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.world.LocationType
import com.grimreich.world.ProceduralLocation
import com.grimreich.world.ProceduralLocationGenerator

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
    val objective: String = "Brak szczegolowych wytycznych.",
    val stabilityImpact: Float = 0.02f,
    val collapseSlowdown: Float = 0.01f
)

object QuestSystem {
    private val quests = linkedMapOf<String, QuestEntry>()
    private var currentSeed: Int = 0
    private const val MAX_TOTAL_QUESTS = 8 // Increased for variety

    fun clear() {
        quests.clear()
        currentSeed = 0
    }

    fun seedIntegratedContent(seed: Int = 1) {
        if (quests.isNotEmpty() && (currentSeed == seed)) return

        val completedIds = GameRepository.state.quest.completedQuests.toSet()
        val activeIds = GameRepository.state.quest.activeQuests.toSet()

        clear()
        currentSeed = seed

        CityCatalogue.seedCanonical()
        
        // 1. CANONICAL QUESTS
        register(QuestEntry(
            id = "quest_north_mist_vision",
            title = "Wizje we Mgle",
            description = "Aelion przemawia przez mgle.",
            cityId = "wybrzeze_polnocne",
            originType = QuestOriginType.LOKACJA_NPC,
            originRefId = "aelion",
            rewardGold = 75,
            objective = "Odszukaj Aeliona we mgle."
        ))
        
        // 2. NARRATIVE TEMPLATES
        val rand = kotlin.random.Random(seed)
        val cityList = CityCatalogue.all()
        
        QuestRegistry.allTemplates.forEach { t ->
            val assignedCityId = t.preferredCityId ?: cityList.random(rand).id
            register(QuestEntry(
                id = t.id,
                title = t.title,
                description = t.description,
                cityId = assignedCityId,
                originType = QuestOriginType.LOKACJA_PROCEDURALNA,
                originRefId = t.category,
                rewardGold = t.baseReward,
                objective = t.objective
            ))
        }

        // 3. BLOOD CHAIN
        QuestRegistry.bloodChain.stages.forEachIndexed { index, s ->
            register(QuestEntry(
                id = s.id,
                title = s.title,
                description = s.description,
                cityId = "rowniny_koronne",
                originType = QuestOriginType.LOKACJA_PROCEDURALNA,
                originRefId = "Chain",
                rewardGold = s.baseReward,
                objective = s.objective,
                requiredQuestIds = if (index > 0) listOf(QuestRegistry.bloodChain.stages[index - 1].id) else emptyList()
            ))
        }

        // 5. RESTORE STATUSES
        quests.values.toList().forEach { q ->
            val status = when {
                completedIds.contains(q.id) -> QuestStatus.UKONCZONE
                activeIds.contains(q.id) -> QuestStatus.AKTYWNE
                else -> q.status
            }
            quests[q.id] = q.copy(status = status)
        }

        limitQuestPool(seed)
    }

    private fun limitQuestPool(seed: Int) {
        val active = quests.values.filter { it.status == QuestStatus.AKTYWNE }
        val availableCandidates = quests.values.filter { it.status == QuestStatus.DOSTEPNE }
        
        val maxAvailable = (MAX_TOTAL_QUESTS - active.size).coerceAtLeast(3) // Ensure at least some are available
        
        if (availableCandidates.size > maxAvailable) {
            val rand = java.util.Random(seed.toLong())
            val toKeep = availableCandidates.shuffled(rand).take(maxAvailable).map { it.id }.toSet()
            availableCandidates.forEach { if (!toKeep.contains(it.id)) quests.remove(it.id) }
        }
    }

    fun register(entry: QuestEntry) {
        quests[entry.id] = entry
    }

    fun all(): List<QuestEntry> = quests.values.toList()

    fun getQuest(id: String): QuestEntry? = quests[id]

    fun availableForCity(cityId: String): List<QuestEntry> =
        quests.values.filter { it.cityId == cityId && it.status == QuestStatus.DOSTEPNE }

    fun activate(questId: String): QuestEntry {
        val quest = quests[questId] ?: error("Nieznane zadanie: $questId")
        val updated = quest.copy(status = QuestStatus.AKTYWNE)
        quests[questId] = updated

        val state = GameRepository.state
        if (!state.quest.activeQuests.contains(questId)) {
            state.quest.activeQuests.add(questId)
        }
        return updated
    }

    fun complete(questId: String): QuestEntry {
        val quest = quests[questId] ?: error("Nieznane zadanie: $questId")
        val updated = quest.copy(status = QuestStatus.UKONCZONE)
        quests[questId] = updated

        val state = GameRepository.state
        state.quest.activeQuests.remove(questId)
        if (!state.quest.completedQuests.contains(questId)) {
            state.quest.completedQuests.add(questId)
        }
        state.gold += quest.rewardGold
        return updated
    }

    fun activeList(): List<String> = quests.values.filter { it.status == QuestStatus.AKTYWNE }.map { it.id }
}
