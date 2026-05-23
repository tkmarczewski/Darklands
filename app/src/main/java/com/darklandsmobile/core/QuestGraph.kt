package com.darklandsmobile.core

// ==================== QUEST GRAPH ====================

enum class QuestStatus {
    LOCKED, AVAILABLE, ACTIVE, COMPLETED, FAILED
}

enum class FailPenaltyType {
    GOLD_LOSS, REPUTATION_LOSS, FAITH_LOSS, SIN_GAIN, NONE
}

data class QuestRequirements(
    val minFaith: Int = 0,
    val minVirtue: Int = 0,
    val minCityReputation: Int = 0,
    val minFactionReputation: Map<String, Int> = emptyMap(),
    val requiredQuestIds: List<String> = emptyList(),
    val minAge: Int = 0,
    val requiredCareer: Career? = null
)

data class QuestRewards(
    val gold: Int = 0,
    val faithBonus: Int = 0,
    val virtueBonus: Int = 0,
    val cityReputationBonus: Int = 0,
    val factionReputationBonus: Map<String, Int> = emptyMap(),
    val divineFavorBonus: Int = 0,
    val unlocksQuestIds: List<String> = emptyList()
)

data class FailState(
    val penaltyType: FailPenaltyType,
    val penaltyAmount: Int = 0,
    val description: String = "",
    val blocksQuestIds: List<String> = emptyList()
)

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val requirements: QuestRequirements = QuestRequirements(),
    val rewards: QuestRewards = QuestRewards(),
    val failState: FailState = FailState(FailPenaltyType.NONE),
    var status: QuestStatus = QuestStatus.LOCKED
)

object QuestGraph {

    val quests = mutableListOf(
        Quest(
            id = "q_start",
            title = "Pierwsze kroki",
            description = "Zacznij swoją podróż i zbadaj okolicę.",
            requirements = QuestRequirements(),
            rewards = QuestRewards(gold = 10, cityReputationBonus = 1),
            failState = FailState(FailPenaltyType.NONE)
        ).also { it.status = QuestStatus.AVAILABLE },
        Quest(
            id = "q_church",
            title = "Służba Kościółowi",
            description = "Pomóż kapłanowi w parafii.",
            requirements = QuestRequirements(minFaith = 2, requiredQuestIds = listOf("q_start")),
            rewards = QuestRewards(gold = 15, faithBonus = 2, virtueBonus = 1,
                factionReputationBonus = mapOf("church" to 3)),
            failState = FailState(FailPenaltyType.REPUTATION_LOSS, penaltyAmount = 2,
                description = "Zawiodłeś Kościół.")
        ),
        Quest(
            id = "q_merchant",
            title = "Kara wana kupiecka",
            description = "Eskortuj karawanę kupca przez las.",
            requirements = QuestRequirements(minCityReputation = 1, requiredQuestIds = listOf("q_start")),
            rewards = QuestRewards(gold = 40, cityReputationBonus = 2,
                factionReputationBonus = mapOf("merchants" to 3)),
            failState = FailState(FailPenaltyType.GOLD_LOSS, penaltyAmount = 20,
                description = "Karawanę zaatakowano.")
        ),
        Quest(
            id = "q_shrine",
            title = "Oczyszczenie kaplicy",
            description = "Oczyść znieważoną kaplicę z ciemnych sił.",
            requirements = QuestRequirements(minFaith = 4, minVirtue = 3,
                factionReputationRequirements = emptyMap(),
                requiredQuestIds = listOf("q_church")),
            rewards = QuestRewards(gold = 25, faithBonus = 3, virtueBonus = 2,
                divineFavorBonus = 3, factionReputationBonus = mapOf("church" to 4)),
            failState = FailState(FailPenaltyType.SIN_GAIN, penaltyAmount = 2,
                description = "Kaplicą nie została oczyszczona.")
        ),
        Quest(
            id = "q_outlaw",
            title = "Prawo pięści",
            description = "Rozprosz bandę rozbojników grasującą na trakcie.",
            requirements = QuestRequirements(minCityReputation = 2,
                requiredQuestIds = listOf("q_merchant")),
            rewards = QuestRewards(gold = 50, cityReputationBonus = 3,
                factionReputationBonus = mapOf("military" to 3, "peasants" to 2)),
            failState = FailState(FailPenaltyType.REPUTATION_LOSS, penaltyAmount = 3,
                description = "Bandyci nadal grasują.")
        )
                ,
        Quest(
            id = "q_raubritter",
            title = "Rycerz-Rabus",
            description = "Raubritter von Eisenbach terroryzuje trakty handlowe. Mieszczanie oferuja nagrode za jego glowe.",
            requirements = QuestRequirements(
                minVirtue = 20,
                minCityReputation = 3,
                requiredQuestIds = listOf("q_outlaw")
            ),
            rewards = QuestRewards(
                gold = 120,
                virtueBonus = 5,
                cityReputationBonus = 5,
                factionReputationBonus = mapOf("military" to 5, "peasants" to 3),
                divineFavorBonus = 2,
                unlocksQuestIds = listOf("q_shrine")
            ),
            failState = FailState(
                FailPenaltyType.REPUTATION_LOSS,
                penaltyAmount = 5,
                description = "Raubritter uciekl - drogi sa nadal niebezpieczne."
            )
        )
    )

    fun findById(id: String) = quests.firstOrNull { it.id == id }

    fun availableQuests(hero: Hero, cityRep: Int, factionRep: FactionReputationSystem,
                        completedIds: Set<String>): List<Quest> {
        return quests.filter { quest ->
            if (quest.status == QuestStatus.COMPLETED || quest.status == QuestStatus.FAILED) return@filter false
            if (quest.status == QuestStatus.ACTIVE) return@filter false
            val req = quest.requirements
            hero.piety >= req.minFaith &&
            hero.virtue >= req.minVirtue &&
            cityRep >= req.minCityReputation &&
            hero.age >= req.minAge &&
            (req.requiredCareer == null || hero.currentCareer == req.requiredCareer) &&
            req.requiredQuestIds.all { it in completedIds } &&
            req.minFactionReputation.all { (fid, minRep) -> factionRep.getReputation(fid) >= minRep }
        }
    }

    fun unlockAfterComplete(completedQuestId: String) {
        val completed = findById(completedQuestId) ?: return
        completed.rewards.unlocksQuestIds.forEach { id ->
            findById(id)?.let { if (it.status == QuestStatus.LOCKED) it.status = QuestStatus.AVAILABLE }
        }
    }
}
