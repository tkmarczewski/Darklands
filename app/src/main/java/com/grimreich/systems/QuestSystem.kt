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
    val objective: String = "Brak szczegółowych wytycznych."
)

object QuestSystem {
    private val quests = linkedMapOf<String, QuestEntry>()
    private var currentSeed: Int = 0

    fun clear() {
        quests.clear()
        currentSeed = 0
    }

    fun seedIntegratedContent(seed: Int = 1) {
        // Force seeding if quests are empty - crucial for first load
        if (quests.isNotEmpty() && (currentSeed == seed)) return
        clear()
        currentSeed = seed

        CityCatalogue.seedCanonical()
        CityEventSystem.seedStage1Events()

        val allCities = CityCatalogue.all()
        android.util.Log.d("QuestSystem", "Seeding quests for ${allCities.size} cities")

        allCities.forEach { city ->
            val cityEvents = CityEventSystem.getEventsForCity(city.id)
            android.util.Log.d("QuestSystem", "City ${city.id} has ${cityEvents.size} events")
            cityEvents.forEach { event ->
                val obj = when(event.id) {
                    "north_mist_vision" -> "Udaj się na Wybrzeże i porozmawiaj z Aelionem we mgle."
                    "north_lost_echo" -> "Zbadaj linię brzegową w poszukiwaniu echa."
                    "crown_blood_toll" -> "Pokonaj wrogów na Równinach i złóż ofiarę z krwi."
                    "crown_iron_forge" -> "Dostarcz rzadką rudę do Ferruna na Równinach."
                    "heart_mirror_truth" -> "Spójrz w lustro w Sercu Krainy i pokonaj swoje odbicie."
                    else -> "Zbadaj wydarzenie: ${event.title}"
                }
                register(
                    QuestEntry(
                        id = "quest_${event.id}",
                        title = event.title,
                        description = event.description,
                        cityId = event.cityId,
                        originType = QuestOriginType.ZDARZENIE_MIEJSKIE,
                        originRefId = event.id,
                        rewardGold = com.grimreich.core.GrimConstants.Economy.QUEST_REWARD_GOLD_STANDARD,
                        objective = obj
                    )
                )
            }
        }

        val generatedLocations = ProceduralLocationGenerator.generate(seed = seed, count = 12)
        android.util.Log.d("QuestSystem", "Generated ${generatedLocations.size} locations")
        generatedLocations.forEach { location ->
            register(location.toQuest())
        }

        // CUSTOM LOCATION QUESTS - Progressive unlock system
        // Plains quest - unlocks after first Coastline quest completed
        register(
            QuestEntry(
                id = "quest_heartland_grain_mystery",
                title = "Tajemnica Zboża",
                description = "Rolnicy mówią o dziwnych znakach na polach. Zbadaj równiny i odkryj prawdę.",
                cityId = "serce_krainy",
                originType = QuestOriginType.LOKACJA_PROCEDURALNA,
                originRefId = "plains_mystery",
                rewardGold = com.grimreich.core.GrimConstants.Economy.QUEST_REWARD_GOLD_CROWN,
                objective = "Udaj się na równiny i zbadaj tajemnicze znaki.",
                requiredQuestIds = listOf("quest_north_mist_vision")
            )
        )
        
        // Forest quest - unlocks after Plains quest progress
        register(
            QuestEntry(
                id = "quest_forest_ancient_grove",
                title = "Pradawny Gaj",
                description = "Stary drwal opowiada o zaginionym gaju, gdzie rosną drzewa pamiętające czasy przed Imperium.",
                cityId = "serce_krainy",
                originType = QuestOriginType.LOKACJA_PROCEDURALNA,
                originRefId = "forest_grove",
                rewardGold = com.grimreich.core.GrimConstants.Economy.QUEST_REWARD_GOLD_FOREST,
                objective = "Znajdź pradawny gaj ukryty w głębi lasu.",
                requiredQuestIds = listOf("quest_heartland_grain_mystery")
            )
        )

        // SEED ENDGAME QUESTS
        EndgameQuestChain.quests.forEach { eq ->
            val obj = when(eq.id) {
                "eq1_signs" -> "Odszukaj 3 kapliczki korupcji w Sercu Krainy."
                "eq2_alliances" -> "Przekonaj frakcję Rycerzy do wsparcia Twojej sprawy."
                "eq3_pilgrimage" -> "Dotrzyj do Bramy Absolutu i dokonaj ostatecznego wyboru."
                else -> "Kontynuuj wątek główny."
            }
            register(
                QuestEntry(
                    id = eq.id,
                    title = "[GŁÓWNY WĄTEK] ${eq.title}",
                    description = eq.description,
                    cityId = "serce_krainy", // Default to heartland for main plot
                    originType = QuestOriginType.ZDARZENIE_MIEJSKIE,
                    originRefId = eq.id,
                    rewardGold = eq.rewards.gold,
                    objective = obj
                )
            )
        }

        // EXAMPLE CITY NPC QUEST - Aelion's request
        register(
            QuestEntry(
                id = "quest_aelion_relic",
                title = "Relikwia Aeliona",
                description = "Tajemniczy mędrzec Aelion prosi o odnalezienie zaginionej relikwii.",
                cityId = "wybrzeze_polnocne",
                originType = QuestOriginType.LOKACJA_NPC,
                originRefId = "aelion",
                rewardGold = 250,
                objective = "Porozmawiaj z Aelionem na Wybrzeżu o jego relikwii."
            )
        )
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
        return updated
    }

    fun complete(questId: String): QuestEntry {
        val quest = quests[questId] ?: error("Nieznane zadanie: $questId")
        if (quest.status == QuestStatus.UKONCZONE) return quest
        
        val updated = quest.copy(status = QuestStatus.UKONCZONE)
        quests[questId] = updated
        
        // DISTRIBUTE REWARDS
        GameRepository.state.gold += quest.rewardGold
        ReputationSystem.modify(quest.cityId, CityFaction.COMMONERS, 5)
        
        // Record in chronicle
        ChronicleSystem.record("Ukończono zadanie: ${quest.title}. Zyskano ${quest.rewardGold} złota.", importance = 2)
        
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
        objective = "Udaj się do lokalizacji i przetrwaj starcie."
    )
    
    // Legacy API removed to avoid confusion
    fun activeList(): List<String> = quests.values.asSequence().filter { it.status == QuestStatus.AKTYWNE }.map { it.id }.toList()
}
