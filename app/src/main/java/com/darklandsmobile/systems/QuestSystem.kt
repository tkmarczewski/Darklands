package com.darklandsmobile.systems

import com.darklandsmobile.world.ProceduralLocation
import com.darklandsmobile.world.ProceduralLocationGenerator

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

        CityEventSystem.seedStage1Events()

        CityEventSystem.getEventsForCity("magdeburg").forEach { event ->
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
            "RUINS" -> "Zbadaj ruiny"
            "RAUBRITTER_CASTLE" -> "Uderz na zamek raubrittera"
            "MONASTERY" -> "Odwiedź klasztor"
            "DUNGEON" -> "Zejdź do lochów"
            else -> "Pomóż pobliskiej osadzie"
        },
        description = "Cel wyprawy: $name.",
        cityId = nearestCityId,
        originType = QuestOriginType.PROCEDURAL_LOCATION,
        originRefId = id,
        rewardGold = rewardGold
    )

    // dla MainActivity:QuestSystem.start("forest_hermit")
    fun start(questId: String): String {
        seedIntegratedContent()
        val resolvedId = if (quests.containsKey(questId)) questId else quests.keys.firstOrNull()
            ?: return "Brak dostępnych questów."
        val quest = activate(resolvedId)
        return "Rozpoczęto quest: ${quest.title}"
    }
}