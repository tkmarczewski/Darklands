package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ProceduralLocation
import com.grimreich.world.ProceduralLocationGenerator

enum class QuestOriginType {
    CITY_EVENT,
    PROCEDURAL_LOCATION
}

enum class QuestStatus {
    AVAILABLE,
    ACTIVE,
    COMPLETED,
    FAILED
}

data class QuestEntry(
    val id: String,
    val title: String,
    val description: String,
    val cityId: String,
    val originType: QuestOriginType,
    val originRefId: String,
    val rewardGold: Int,
    val status: QuestStatus = QuestStatus.AVAILABLE
)

/**
 * Integration layer joining city events and procedural locations into a single quest feed.
 */
object QuestSystem {
    private val quests = linkedMapOf<String, QuestEntry>()
    private var currentSeed: Int = 0

    fun clear() {
        quests.clear()
        currentSeed = 0
    }

    fun seedIntegratedContent(seed: Int = 1) {
        if (quests.isNotEmpty() && currentSeed == seed) return
        clear()
        currentSeed = seed

        CityCatalogue.seedSprint1()
        CityEventSystem.seedStage1Events()

        // Seed events for ALL cities in catalogue
        CityCatalogue.all().forEach { city ->
            CityEventSystem.getEventsForCity(city.id).forEach { event ->
                register(
                    QuestEntry(
                        id = "quest_${event.id}",
                        title = event.title,
                        description = event.description,
                        cityId = event.cityId,
                        originType = QuestOriginType.CITY_EVENT,
                        originRefId = event.id,
                        rewardGold = event.rewardGold
                    )
                )
            }
        }

        val generatedLocations = ProceduralLocationGenerator.generate(seed = seed, count = 8)
        generatedLocations.forEach { location ->
            register(location.toQuest())
        }
    }

    fun register(entry: QuestEntry) {
        quests[entry.id] = entry
    }

    fun all(): List<QuestEntry> = quests.values.toList()

    fun availableForCity(cityId: String): List<QuestEntry> =
        quests.values.filter { it.cityId == cityId && it.status == QuestStatus.AVAILABLE }

    fun activate(questId: String): QuestEntry {
        val quest = quests[questId] ?: error("Unknown quest: $questId")
        val updated = quest.copy(status = QuestStatus.ACTIVE)
        quests[questId] = updated
        return updated
    }

    fun complete(questId: String): QuestEntry {
        val quest = quests[questId] ?: error("Unknown quest: $questId")
        val updated = quest.copy(status = QuestStatus.COMPLETED)
        quests[questId] = updated
        return updated
    }

    private fun ProceduralLocation.toQuest(): QuestEntry = QuestEntry(
        id = "quest_${id}",
        title = when (type.name) {
            "RUINS"            -> "Zbadaj ruiny"
            "RAUBRITTER_CASTLE"-> "Uderz na zamek raubrittera"
            "MONASTERY"        -> "Odwiedź klasztor"
            "DUNGEON"          -> "Zejdź do lochów"
            else               -> "Pomóż pobliskiej osadzie"
        },
        description = "Cel wyprawy: $name.",
        cityId = nearestCityId,
        originType = QuestOriginType.PROCEDURAL_LOCATION,
        originRefId = id,
        rewardGold = rewardGold
    )

    // LEGACY API remains for compatibility
    private val legacyActiveQuests = linkedMapOf<String, Int>() 
    private val legacyCompletedQuests = mutableListOf<String>()

    private fun syncToRepo() {
        val q = GameRepository.state.quest
        q.activeQuests.clear()
        q.activeQuests.addAll(legacyActiveQuests.keys)
        q.completedQuests.clear()
        q.completedQuests.addAll(legacyCompletedQuests)
        q.questProgress.clear()
        q.questProgress.putAll(legacyActiveQuests)
    }

    fun start(questId: String): String {
        if (legacyActiveQuests.containsKey(questId)) {
            val msg = "Quest $questId jest już aktywny."
            syncToRepo()
            return msg
        }
        legacyActiveQuests[questId] = 0
        syncToRepo()
        return "Rozpoczęto quest: $questId"
    }

    fun advance(questId: String, steps: Int = 1): String {
        val current = legacyActiveQuests[questId] ?: return "Quest $questId nie jest aktywny."
        val newProgress = (current + steps).coerceAtMost(3)
        legacyActiveQuests[questId] = newProgress
        if (newProgress >= 3) {
            legacyActiveQuests.remove(questId)
            legacyCompletedQuests.add(questId)
        }
        syncToRepo()
        return "Quest $questId: postęp ${newProgress}/3"
    }

    fun activeList(): List<String> = legacyActiveQuests.keys.toList()

    fun finalQuestSummary(): String = "Summary functionality migrated to QuestJournalSystem."
}
