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
    val objective: String = "Brak szczegółowych wytycznych.",
    val stabilityImpact: Float = 0.02f, // Positive increases stability
    val collapseSlowdown: Float = 0.01f  // Reduces progress
)

object QuestSystem {
    private val quests = linkedMapOf<String, QuestEntry>()
    private var currentSeed: Int = 0
    private const val MAX_AVAILABLE_QUESTS = 5

    fun clear() {
        quests.clear()
        currentSeed = 0
    }

    fun seedIntegratedContent(seed: Int = 1) {
        if (quests.isNotEmpty() && (currentSeed == seed)) return
        
        // CRITICAL: Capture current persistent status from GameState
        val completedIds = GameRepository.state.quest.completedQuests.toSet()
        val activeIds = GameRepository.state.quest.activeQuests.toSet()

        clear()
        currentSeed = seed

        CityCatalogue.seedCanonical()
        CityEventSystem.seedStage1Events()

        // 1. REGISTER CANONICAL PROPHECY QUESTS
        register(QuestEntry(
            id = "quest_north_mist_vision", title = "Wizje we Mgle", description = "Aelion przemawia przez mgłę.", cityId = "wybrzeze_polnocne",
            originType = QuestOriginType.LOKACJA_NPC, originRefId = "aelion", rewardGold = 75, objective = "Odszukaj Aeliona we mgle."
        ))
        register(QuestEntry(
            id = "quest_aelion_relic", title = "Relikwia Aeliona", description = "Odzyskaj skradziony odłamek.", cityId = "wybrzeze_polnocne",
            originType = QuestOriginType.LOKACJA_NPC, originRefId = "aelion", rewardGold = 250, objective = "Zwróć relikwię Aelionowi."
        ))

        // 2. REGISTER 40+ NARRATIVE TEMPLATES (categorized)
        QuestRegistry.allTemplates.forEach { t ->
            register(QuestEntry(
                id = t.id, title = t.title, description = t.description, 
                cityId = t.preferredCityId ?: CityCatalogue.all().random(kotlin.random.Random(seed + t.id.hashCode())).id,
                originType = QuestOriginType.LOKACJA_PROCEDURALNA, originRefId = t.category, rewardGold = t.baseReward, objective = t.objective
            ))
        }

        // 3. REGISTER BLOOD CHAIN
        QuestRegistry.bloodChain.stages.forEachIndexed { index, s ->
            register(QuestEntry(
                id = s.id, title = s.title, description = s.description, cityId = "rowniny_koronne",
                originType = QuestOriginType.LOKACJA_PROCEDURALNA, originRefId = "Chain", rewardGold = s.baseReward, objective = s.objective,
                requiredQuestIds = if (index > 0) listOf(QuestRegistry.bloodChain.stages[index-1].id) else emptyList()
            ))
        }

        // 4. REGISTER CHAIN: THE VERDICT NO ONE ISSUED
        QuestRegistry.verdictChain.stages.forEachIndexed { index, s ->
            register(QuestEntry(
                id = s.id, title = s.title, description = s.description, cityId = "serce_krainy",
                originType = QuestOriginType.LOKACJA_PROCEDURALNA, originRefId = "Verdict", rewardGold = s.baseReward, objective = s.objective,
                requiredQuestIds = if (index > 0) listOf(QuestRegistry.verdictChain.stages[index-1].id) else emptyList()
            ))
        }

        // 5. RESTORE PERSISTENT STATUSES
        quests.values.toList().forEach { q ->
            val status = when {
                completedIds.contains(q.id) -> QuestStatus.UKONCZONE
                activeIds.contains(q.id) -> QuestStatus.AKTYWNE
                else -> QuestStatus.DOSTEPNE
            }
            quests[q.id] = q.copy(status = status)
        }

        // 6. LIMIT POOL (Limit available + active to 5 TOTAL)
        limitQuestPool(seed)
    }

    private fun limitQuestPool(seed: Int) {
        val active = quests.values.filter { it.status == QuestStatus.AKTYWNE }
        val available = quests.values.filter { it.status == QuestStatus.DOSTEPNE }
        
        // Narrative chains (Blood/Verdict) and canonical ones are ALWAYS preserved if active
        // But if they are just AVAILABLE, they count towards the pool or are hidden by logic
        val specialIds = setOf("quest_north_mist_vision", "quest_aelion_relic")
        
        // Procedural ones and available chains are the pool
        val poolCandidate = available.filter { 
            !specialIds.contains(it.id) && !it.id.contains("verdict") && it.originRefId != "Chain" 
        }
        
        // Total allowed available = 5 - currently active
        val slotsRemaining = (MAX_AVAILABLE_QUESTS - active.size).coerceAtLeast(0)
        
        if (poolCandidate.size > slotsRemaining) {
            val rand = java.util.Random(seed.toLong())
            val toKeep = poolCandidate.shuffled(rand).take(slotsRemaining).map { it.id }.toSet()
            
            poolCandidate.forEach { if (!toKeep.contains(it.id)) quests.remove(it.id) }
        }
        
        // VERDICT HIDER: Hide Verdict quests unless their Stage 4 NPC trigger has happened (visit 7+)
        val cityCount = GameRepository.state.world.cityEntryCount
        QuestRegistry.verdictChain.stages.forEach { stage ->
            val q = quests[stage.id] ?: return@forEach
            // Only hide if it's NOT already started (Active or Done)
            if (q.status == QuestStatus.DOSTEPNE && cityCount < 7) {
                quests.remove(q.id)
            }
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
        
        // Sync with GameState - Ensure it's added to the actual persistent list
        val state = GameRepository.state
        if (!state.quest.activeQuests.contains(questId)) {
            state.quest.activeQuests.add(questId)
        }
        
        // DYNAMIC LOCATION: If quest has a preferred city, "discover" it if not already there
        val template = QuestRegistry.allTemplates.find { it.id == questId } ?: QuestRegistry.verdictChain.stages.find { it.id == questId }
        template?.preferredCityId?.let { cityId ->
            if (!state.world.discoveredLocations.contains(cityId)) {
                state.world.discoveredLocations.add(cityId)
            }
        }
        if (!state.world.discoveredLocations.contains(quest.cityId)) {
            state.world.discoveredLocations.add(quest.cityId)
        }
        
        return updated
    }

    fun complete(questId: String): QuestEntry {
        val quest = quests[questId] ?: error("Nieznane zadanie: $questId")
        if (quest.status == QuestStatus.UKONCZONE) return quest
        
        val updated = quest.copy(status = QuestStatus.UKONCZONE)
        quests[questId] = updated
        
        // Sync with GameState
        val state = GameRepository.state
        state.quest.activeQuests.remove(questId)
        if (!state.quest.completedQuests.contains(questId)) {
            state.quest.completedQuests.add(questId)
        }
        
        // DISTRIBUTE REWARDS
        state.gold += quest.rewardGold
        ReputationSystem.modify(quest.cityId, CityFaction.COMMONERS, 5)
        
        // APPLY WORLD IMPACT
        val stabilityGain = (quest.stabilityImpact * 100).toInt()
        state.world.globalStability = (state.world.globalStability + stabilityGain).coerceIn(0, 100)
        state.world.collapseProgress = (state.world.collapseProgress - quest.collapseSlowdown).coerceAtLeast(0f)
        
        // Record in chronicle
        ChronicleSystem.record("Ukończono zadanie: ${quest.title}. Stabilność świata: ${state.world.globalStability}%", importance = 2)
        
        return updated
    }

    private fun ProceduralLocation.toQuest(): QuestEntry = QuestEntry(
        id = "quest_$id",
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
        rewardGold = rewardGold,
        objective = "Udaj się do lokalizacji i przetrwaj starcie.",
        stabilityImpact = 0.05f
    )
    
    // Legacy API removed to avoid confusion
    fun activeList(): List<String> = quests.values.asSequence().filter { it.status == QuestStatus.AKTYWNE }.map { it.id }.toList()
}
