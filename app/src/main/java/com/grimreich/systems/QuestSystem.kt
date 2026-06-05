package com.grimreich.systems

import com.grimreich.core.GameRepository
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

        CityEventSystem.seedStage1Events()

        CityEventSystem.getEventsForCity("grimhold").forEach { event ->
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
            "RUINS"            -> "Zbadaj ruiny"
            "RAUBRITTER_CASTLE"-> "Uderz na zamek raubrittera"
            "MONASTERY"        -> "Odwiedz klasztor"
            "DUNGEON"          -> "Zejdz do lochow"
            else               -> "Pomoz pobliskiej osadzie"
        },
        description = "Cel wyprawy: $name.",
        cityId = nearestCityId,
        originType = QuestOriginType.PROCEDURAL_LOCATION,
        originRefId = id,
        rewardGold = rewardGold
    )

    // LEGACY API dla testów:

    private val legacyActiveQuests = linkedMapOf<String, Int>() // questId -> progress 0..3
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
            val msg = "Quest $questId jest juz aktywny."
            syncToRepo()
            return msg
        }

        legacyActiveQuests[questId] = 0

        val title = when (questId) {
            "forest_hermit" -> "Znajdz pustelnika w lesie"
            "bandit_camp"   -> "Rozprosz oboz bandytow"
            "lost_relic"    -> "Odnajdz zaginiony relikt"
            else            -> questId
        }

        val msg = "Rozpoczeto quest: $title"
        syncToRepo()
        return msg
    }

    fun advance(questId: String, steps: Int = 1): String {
        val current = legacyActiveQuests[questId]
            ?: run {
                val msg = "Quest $questId nie jest aktywny."
                syncToRepo()
                return msg
            }

        val newProgress = (current + steps).coerceAtMost(3)
        legacyActiveQuests[questId] = newProgress

        val title = when (questId) {
            "forest_hermit" -> "Znajdz pustelnika w lesie"
            "bandit_camp"   -> "Rozprosz oboz bandytow"
            "lost_relic"    -> "Odnajdz zaginiony relikt"
            else            -> questId
        }

        val msg = if (newProgress >= 3) {
            legacyActiveQuests.remove(questId)
            if (!legacyCompletedQuests.contains(questId)) {
                legacyCompletedQuests.add(questId)
            }
            "Quest $title ukonczony."
        } else {
            "Quest $title: postep ${newProgress}/3"
        }

        syncToRepo()
        return msg
    }

    fun activeList(): List<String> = legacyActiveQuests.keys.toList()

    fun finalQuestSummary(): String {
        val sb = StringBuilder()

        if (legacyActiveQuests.isEmpty()) {
            sb.append("Aktywne questy:\n  brak\n")
        } else {
            sb.append("Aktywne questy:\n")
            for ((id, progress) in legacyActiveQuests) {
                val title = when (id) {
                    "forest_hermit" -> "Znajdz pustelnika w lesie"
                    "bandit_camp"   -> "Rozprosz oboz bandytow"
                    "lost_relic"    -> "Odnajdz zaginiony relikt"
                    else            -> id
                }
                sb.append("  $title (${progress}/3)\n")
            }
        }

        if (legacyCompletedQuests.isEmpty()) {
            sb.append("Ukonczone questy:\n  brak")
        } else {
            sb.append("Ukonczone questy:\n")
            for (id in legacyCompletedQuests) {
                val title = when (id) {
                    "forest_hermit" -> "Znajdz пустelnika w lesie"
                    "bandit_camp"   -> "Rozprosz oboz bandytow"
                    "lost_relic"    -> "Odnajdz zaginiony relikt"
                    else            -> id
                }
                sb.append("  $title\n")
            }
        }

        return sb.toString()
    }
}