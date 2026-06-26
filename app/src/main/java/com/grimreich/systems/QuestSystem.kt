package com.grimreich.systems

import com.grimreich.core.GameRepository
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

enum class QuestStatus {
    DOSTEPNE, AKTYWNE, CEL_OSIAGNIETY, UKONCZONE
}

data class QuestEntry(
    val id: String,
    val title: String,
    val description: String,
    val objective: String,
    val cityId: String,
    val rewardGold: Int,
    var status: QuestStatus = QuestStatus.DOSTEPNE,
    val originRefId: String = "mystic",
    val isOutsideCity: Boolean = false,
    val nextQuestId: String? = null, // For chains
    val hasCombat: Boolean = false, // true if quest involves a battle
    val category: String = "Normal",
    val nextLocationHint: String? = null,
    val nextNpcHint: String? = null,
    val factionRewardId: String? = null,
    val factionRewardAmount: Int = 0
)

@Singleton
class QuestSystem @Inject constructor(
    private val gameRepositoryProvider: Lazy<GameRepository>
) {
    private val gameRepository get() = gameRepositoryProvider.get()
    private val allQuests = mutableMapOf<String, QuestEntry>()

    fun register(quest: QuestEntry) {
        allQuests[quest.id] = quest
    }

    fun getQuest(id: String): QuestEntry? = allQuests[id]

    fun all(): List<QuestEntry> = allQuests.values.toList()

    fun activate(id: String): QuestEntry {
        val quest = allQuests[id] ?: throw IllegalArgumentException("No such quest: $id")
        quest.status = QuestStatus.AKTYWNE
        gameRepository.updateState { state ->
            if (!state.quest.activeQuests.contains(id)) {
                state.quest.activeQuests.add(id)
            }
        }
        return quest
    }

    fun markObjectiveComplete(id: String) {
        val quest = allQuests[id] ?: return
        if (quest.status == QuestStatus.AKTYWNE) {
            quest.status = QuestStatus.CEL_OSIAGNIETY
            gameRepository.log("""
                Cel osiągnięty: ${quest.title}. Twoja dłoń drży, gdy odkładasz broń lub zamykasz księgę. 
                Duszny zapach spalonego Echa unosi się w powietrzu, a Ty czujesz na plecach spojrzenie Skrybów. 
                To jeszcze nie koniec. Wróć do zleceniodawcy, by odebrać to, co Ci obiecano.
            """.trimIndent())
            gameRepository.persistCurrentState()
        }
    }

    fun complete(id: String): QuestEntry {
        val quest = allQuests[id] ?: throw IllegalArgumentException("No such quest: $id")
        // No longer restricted to only CEL_OSIAGNIETY for safety/legacy reasons, but primarily called from dialogue now
        quest.status = QuestStatus.UKONCZONE
        
        gameRepository.updateState { state ->
            state.quest.activeQuests.remove(id)
            if (!state.quest.completedQuests.contains(id)) {
                state.quest.completedQuests.add(id)
            }
            state.gold += quest.rewardGold
            
            // Faction Rewards
            quest.factionRewardId?.let { factionId ->
                val current = state.reputation.globalFactions[factionId] ?: 0
                state.reputation.globalFactions[factionId] = current + quest.factionRewardAmount
                gameRepository.log("Więzi z frakcją: $factionId zacieśniły się (+${quest.factionRewardAmount}). Widzą w Tobie kogoś więcej niż tylko najemnika.")
                
                // Handle Rivalries (Simple logic: if helping Dawn, Inquisition dislikes it slightly)
                handleRivalries(state, factionId, quest.factionRewardAmount)
            }

            // Handle chains: activate next quest
            quest.nextQuestId?.let { nextId ->
                if (!state.quest.activeQuests.contains(nextId) && !state.quest.completedQuests.contains(nextId)) {
                    allQuests[nextId]?.status = QuestStatus.AKTYWNE
                    val nextQuest = allQuests[nextId]
                    val hint = if (nextQuest?.nextLocationHint != null) {
                        " Twoja droga prowadzi teraz do: ${nextQuest.nextLocationHint} (Szukaj: ${nextQuest.nextNpcHint})"
                    } else ""
                    gameRepository.log("Cień Twoich czynów rzuca nowe wyzwanie: ${nextQuest?.title}.$hint")
                    state.quest.activeQuests.add(nextId)
                }
            }
        }
        
        gameRepository.log("Zadanie '${quest.title}' dobiegło końca. Brzęk złota w mieszku miesza się z poczuciem spełnionego (lub zignorowanego) obowiązku.")
        return quest
    }

    fun availableForCity(cityId: String, excludeIds: Set<String> = emptySet()): List<QuestEntry> {
        return allQuests.values.filter { 
            (it.cityId == cityId) && (it.status == QuestStatus.DOSTEPNE) && !excludeIds.contains(it.id)
        }
    }

    fun clear() {
        allQuests.clear()
    }

    private fun handleRivalries(state: com.grimreich.core.GameState, factionId: String, amount: Int) {
        val rivals = when (factionId.lowercase()) {
            "zakon", "dawn" -> listOf("inkwizycja")
            "inkwizycja", "inquisition" -> listOf("zakon")
            "pustka", "void" -> listOf("zakon", "inkwizycja")
            else -> emptyList()
        }
        
        rivals.forEach { rivalId ->
            val current = state.reputation.globalFactions[rivalId] ?: 0
            state.reputation.globalFactions[rivalId] = current - (amount / 2)
            if (amount >= 10) {
                 gameRepository.log("Twoje działania nie spodobały się frakcji: $rivalId")
            }
        }
    }

    fun seedIntegratedContent() {
        if (allQuests.isNotEmpty()) return
        
        val canonicalCities = listOf(
            "wybrzeze_polnocne", 
            "rowniny_koronne", 
            "twierdza_zakonu", 
            "serce_krainy", 
            "poludniowe_ruiny", 
            "gory_poludniowe", 
            "pogranicze_stepowe", 
            "ziemie_dzikie"
        )

        // Seed all templates from QuestRegistry - distributing them across cities
        QuestRegistry.allTemplates.forEachIndexed { index, template ->
            register(
                QuestEntry(
                    id = template.id,
                    title = template.title,
                    description = template.description,
                    objective = template.objective,
                    cityId = template.preferredCityId ?: canonicalCities[index % canonicalCities.size],
                    rewardGold = template.baseReward,
                    originRefId = when (template.category) {
                        "Intrigue" -> "merchant"
                        "Anomaly" -> "mystic"
                        "Beast" -> "guard"
                        "Drama" -> "zealot"
                        else -> "mystic"
                    },
                    factionRewardId = when (template.category) {
                        "Intrigue" -> "pustka"
                        "Anomaly" -> "milczenie"
                        "Beast" -> "inkwizycja"
                        "Drama" -> "zakon"
                        else -> null
                    },
                    factionRewardAmount = 15,
                    isOutsideCity = (template.category == "Anomaly" || template.category == "Beast"),
                    hasCombat = template.enemyStats != null,
                    category = template.category
                )
            )
        }

        // Seed blood chain
        QuestRegistry.bloodChain.stages.forEachIndexed { i, template ->
             register(
                QuestEntry(
                    id = template.id,
                    title = template.title,
                    description = template.description,
                    objective = template.objective,
                    cityId = "wybrzeze_polnocne",
                    rewardGold = template.baseReward,
                    status = if (i == 0) QuestStatus.DOSTEPNE else QuestStatus.AKTYWNE, // Only first is available initially
                    originRefId = "mystic",
                    hasCombat = template.enemyStats != null,
                    isOutsideCity = true,
                    category = "Chain",
                    factionRewardId = "zakon",
                    factionRewardAmount = 10,
                    nextQuestId = if (i < QuestRegistry.bloodChain.stages.size - 1) QuestRegistry.bloodChain.stages[i+1].id else null,
                    nextLocationHint = if (i < QuestRegistry.bloodChain.stages.size - 1) "Wybrzeże Północne" else null,
                    nextNpcHint = if (i < QuestRegistry.bloodChain.stages.size - 1) "Mistyk" else null
                )
            )
        }

        // Seed verdict chain
        QuestRegistry.verdictChain.stages.forEachIndexed { i, template ->
            register(
                QuestEntry(
                    id = template.id,
                    title = template.title,
                    description = template.description,
                    objective = template.objective,
                    cityId = "twierdza_zakonu",
                    rewardGold = template.baseReward,
                    originRefId = "guard",
                    hasCombat = template.enemyStats != null,
                    isOutsideCity = (template.id != "q_verdict_1"),
                    category = "Verdict",
                    factionRewardId = "inkwizycja",
                    factionRewardAmount = 12,
                    nextQuestId = if (i < QuestRegistry.verdictChain.stages.size - 1) QuestRegistry.verdictChain.stages[i+1].id else null,
                    nextLocationHint = if (i < QuestRegistry.verdictChain.stages.size - 1) "Twierdza Zakonu" else null,
                    nextNpcHint = if (i < QuestRegistry.verdictChain.stages.size - 1) "Strażnik" else null
                )
            )
        }
    }
}
