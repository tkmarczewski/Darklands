package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.world.LocationType
import com.grimreich.world.ProceduralLocation
import com.grimreich.world.ProceduralLocationGenerator

enum class QuestOriginType {
    ZDARZENIE_MIEJSKIE,
    LOKACJA_PROCEDURALNA
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
    val status: QuestStatus = QuestStatus.DOSTEPNE
)

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

        CityCatalogue.all().forEach { city ->
            CityEventSystem.getEventsForCity(city.id).forEach { event ->
                register(
                    QuestEntry(
                        id = "quest_${event.id}",
                        title = event.title,
                        description = event.description,
                        cityId = event.cityId,
                        originType = QuestOriginType.ZDARZENIE_MIEJSKIE,
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
        quests.values.filter { it.cityId == cityId && it.status == QuestStatus.DOSTEPNE }

    fun activate(questId: String): QuestEntry {
        val quest = quests[questId] ?: error("Nieznane zadanie: $questId")
        val updated = quest.copy(status = QuestStatus.AKTYWNE)
        quests[questId] = updated
        return updated
    }

    fun complete(questId: String): QuestEntry {
        val quest = quests[questId] ?: error("Nieznane zadanie: $questId")
        val updated = quest.copy(status = QuestStatus.UKONCZONE)
        quests[questId] = updated
        return updated
    }

    private fun ProceduralLocation.toQuest(): QuestEntry = QuestEntry(
        id = "quest_${id}",
        title = when (type) {
            LocationType.ZGLISZCZA      -> "Zbadaj Zgliszcza"
            LocationType.MROCZNY_ZAKON  -> "Oczyść Mroczny Zakon"
            LocationType.TWIERDZA_CIENIA -> "Uderz na Twierdzę Cienia"
            LocationType.KATAKUMBY_MROKU -> "Zejdź do Katakumb"
            LocationType.KAPLICZKA_KRWI  -> "Zbezcześć Kapliczkę Krwi"
        },
        description = "Cel wyprawy: $name.",
        cityId = nearestCityId,
        originType = QuestOriginType.LOKACJA_PROCEDURALNA,
        originRefId = id,
        rewardGold = rewardGold
    )
    
    // Legacy API removed to avoid confusion
    fun activeList(): List<String> = quests.values.filter { it.status == QuestStatus.AKTYWNE }.map { it.id }
}
