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
    private const val MAX_TOTAL_QUESTS = 5

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
        CityEventSystem.seedStage1Events()

        // 1. CANONICAL QUESTS - always available
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
        register(QuestEntry(
            id = "quest_aelion_relic",
            title = "Relikwia Aeliona",
            description = "Odzyskaj skradziony odlamek.",
            cityId = "wybrzeze_polnocne",
            originType = QuestOriginType.LOKACJA_NPC,
            originRefId = "aelion",
            rewardGold = 250,
            objective = "Zwroc relikwie Aelionowi."
        ))

        // 2. NARRATIVE TEMPLATES
        QuestRegistry.allTemplates.forEach { t ->
            register(QuestEntry(
                id = t.id,
                title = t.title,
                description = t.description,
                cityId = t.preferredCityId ?: CityCatalogue.all().random(kotlin.random.Random(seed + t.id.hashCode())).id,
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

        // 4. VERDICT CHAIN - rejestrujemy wszystkie etapy jako PRZERWANE;
        //    tylko etap 0 staje sie DOSTEPNE gdy cityCount >= 7 (auto-trigger w fazie 4)
        //    kolejne etapy odblokowuja sie po ukonczeniu poprzedniego w activate()
        QuestRegistry.verdictChain.stages.forEachIndexed { index, s ->
            register(QuestEntry(
                id = s.id,
                title = s.title,
                description = s.description,
                cityId = "serce_krainy",
                originType = QuestOriginType.LOKACJA_PROCEDURALNA,
                originRefId = "Verdict",
                rewardGold = s.baseReward,
                objective = s.objective,
                status = QuestStatus.PRZERWANE, // domyslnie ukryte
                requiredQuestIds = if (index > 0) listOf(QuestRegistry.verdictChain.stages[index - 1].id) else emptyList()
            ))
        }

        // 5. PRZYWROC STATUSY
        quests.values.toList().forEach { q ->
            val status = when {
                completedIds.contains(q.id) -> QuestStatus.UKONCZONE
                activeIds.contains(q.id) -> QuestStatus.AKTYWNE
                else -> q.status // zachowaj domyslny (np. PRZERWANE dla Verdict)
            }
            quests[q.id] = q.copy(status = status)
        }

        // 6. VERDICT PHASE 4 AUTO-TRIGGER:
        //    jesli juz odwiedzono >= 7 miast I etap 0 Verdict nie jest jeszcze aktywny/ukonczony,
        //    udostepnij etap 0 jako DOSTEPNE
        val cityCount = GameRepository.state.world.cityEntryCount
        if (cityCount >= 7) {
            unlockNextVerdictStage(completedIds)
        }

        // 7. LIMIT PULI - lacznie max MAX_TOTAL_QUESTS widocznych (DOSTEPNE + AKTYWNE)
        limitQuestPool(seed)
    }

    /**
     * Odblokowuje kolejny etap Verdict: jesli poprzedni etap jest ukonczony
     * (lub nie ma poprzedniego), ustaw status na DOSTEPNE.
     */
    fun unlockNextVerdictStage(completedIds: Set<String> = GameRepository.state.quest.completedQuests.toSet()) {
        QuestRegistry.verdictChain.stages.forEachIndexed { index, s ->
            val q = quests[s.id] ?: return@forEachIndexed
            if (q.status == QuestStatus.PRZERWANE) {
                val prereqMet = index == 0 || completedIds.contains(QuestRegistry.verdictChain.stages[index - 1].id)
                if (prereqMet) {
                    quests[s.id] = q.copy(status = QuestStatus.DOSTEPNE)
                    return // odblokowuj tylko jeden etap na raz
                }
            }
        }
    }

    private fun limitQuestPool(seed: Int) {
        val specialIds = setOf("quest_north_mist_vision", "quest_aelion_relic")
        val verdictIds = QuestRegistry.verdictChain.stages.map { it.id }.toSet()
        val bloodIds = QuestRegistry.bloodChain.stages.map { it.id }.toSet()

        // Policz aktualnie widoczne (DOSTEPNE + AKTYWNE)
        val active = quests.values.filter { it.status == QuestStatus.AKTYWNE }
        val available = quests.values.filter { it.status == QuestStatus.DOSTEPNE }

        val totalVisible = active.size + available.size

        if (totalVisible <= MAX_TOTAL_QUESTS) return

        // Kandydaci do usuniecia: proceduralne (nie specjalne, nie Verdict, nie Blood aktywne/ukonczone)
        val removable = available.filter {
            !specialIds.contains(it.id) &&
            !verdictIds.contains(it.id) &&
            !bloodIds.contains(it.id)
        }

        val toRemoveCount = totalVisible - MAX_TOTAL_QUESTS
        if (removable.isEmpty()) return

        val rand = java.util.Random(seed.toLong())
        val toRemove = removable.shuffled(rand).take(toRemoveCount)
        toRemove.forEach { quests.remove(it.id) }
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

        // ODKRYJ LOKACJE: canonical city questa
        if (!state.world.discoveredLocations.contains(quest.cityId)) {
            state.world.discoveredLocations.add(quest.cityId)
        }

        // ODKRYJ LOKACJE: preferowana city z template (dla procedural)
        val template = QuestRegistry.allTemplates.find { it.id == questId }
            ?: QuestRegistry.verdictChain.stages.find { it.id == questId }
        template?.preferredCityId?.let { cityId ->
            if (!state.world.discoveredLocations.contains(cityId)) {
                state.world.discoveredLocations.add(cityId)
            }
        }

        // ODKRYJ LOKACJE NIEKANONICZNE: jesli quest jest procedural lokacja, dodaj originRefId jako lokacje
        if (quest.originType == QuestOriginType.LOKACJA_PROCEDURALNA && quest.originRefId.isNotBlank()) {
            val locId = quest.originRefId
            if (!state.world.discoveredLocations.contains(locId)) {
                state.world.discoveredLocations.add(locId)
            }
        }

        return updated
    }

    fun complete(questId: String): QuestEntry {
        val quest = quests[questId] ?: error("Nieznane zadanie: $questId")
        if (quest.status == QuestStatus.UKONCZONE) return quest

        val updated = quest.copy(status = QuestStatus.UKONCZONE)
        quests[questId] = updated

        val state = GameRepository.state
        state.quest.activeQuests.remove(questId)
        if (!state.quest.completedQuests.contains(questId)) {
            state.quest.completedQuests.add(questId)
        }

        state.gold += quest.rewardGold
        ReputationSystem.modify(quest.cityId, CityFaction.COMMONERS, 5)

        val stabilityGain = (quest.stabilityImpact * 100).toInt()
        state.world.globalStability = (state.world.globalStability + stabilityGain).coerceIn(0, 100)
        state.world.collapseProgress = (state.world.collapseProgress - quest.collapseSlowdown).coerceAtLeast(0f)

        ChronicleSystem.record(
            "Ukonczono zadanie: ${quest.title}. Stabilnosc swiata: ${state.world.globalStability}%",
            importance = 2
        )

        // Po ukonczeniu etapu Verdict - odblokuj kolejny
        if (quest.originRefId == "Verdict") {
            unlockNextVerdictStage()
        }
        // Po ukonczeniu etapu Blood - odblokuj kolejny etap lancucha
        if (quest.originRefId == "Chain") {
            unlockNextBloodStage()
        }

        return updated
    }

    fun unlockNextBloodStage(completedIds: Set<String> = GameRepository.state.quest.completedQuests.toSet()) {
        QuestRegistry.bloodChain.stages.forEachIndexed { index, s ->
            val q = quests[s.id] ?: return@forEachIndexed
            if (q.status == QuestStatus.PRZERWANE) {
                val prereqMet = index == 0 || completedIds.contains(QuestRegistry.bloodChain.stages[index - 1].id)
                if (prereqMet) {
                    quests[s.id] = q.copy(status = QuestStatus.DOSTEPNE)
                    return
                }
            }
        }
    }

    fun activeList(): List<String> = quests.values.asSequence()
        .filter { it.status == QuestStatus.AKTYWNE }
        .map { it.id }
        .toList()
}
