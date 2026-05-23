package com.darklandsmobile.systems

data class EndgameQuest(
    val id: String,
    val title: String,
    val description: String,
    val requirements: EndgameRequirements,
    val rewards: EndgameRewards,
    var status: EndgameQuestStatus = EndgameQuestStatus.LOCKED
)

enum class EndgameQuestStatus { LOCKED, AVAILABLE, ACTIVE, COMPLETED, FAILED }

data class EndgameRequirements(
    val minFaith: Int = 0,
    val minVirtue: Int = 0,
    val minCityReputation: Int = 0,
    val minFactionReputation: Int = 0,
    val requiredQuestIds: List<String> = emptyList()
)

data class EndgameRewards(
    val gold: Int = 0,
    val faithBonus: Int = 0,
    val reputationBonus: Int = 0,
    val divineFavorBonus: Int = 0
)

object EndgameQuestChain {
    val quests = listOf(
        EndgameQuest(
            id = "eq1_signs",
            title = "Signs of Corruption",
            description = "Odkryj wpływy kultu w mieście, klasztorze i na trakcie.",
            requirements = EndgameRequirements(minFaith = 3, minCityReputation = 2),
            rewards = EndgameRewards(gold = 50, faithBonus = 2, reputationBonus = 3)
        ),
        EndgameQuest(
            id = "eq2_alliances",
            title = "Broken Alliances",
            description = "Zdobadź wsparcie frakcji lub obejdź wrogów.",
            requirements = EndgameRequirements(
                minFactionReputation = 3,
                requiredQuestIds = listOf("eq1_signs")
            ),
            rewards = EndgameRewards(gold = 80, reputationBonus = 5, divineFavorBonus = 2)
        ),
        EndgameQuest(
            id = "eq3_pilgrimage",
            title = "Pilgrimage to the Gate",
            description = "Dotrzyj do ostatniego miejsca kultu przez wiarę i łaskę.",
            requirements = EndgameRequirements(
                minFaith = 6,
                minVirtue = 4,
                requiredQuestIds = listOf("eq2_alliances")
            ),
            rewards = EndgameRewards(gold = 120, faithBonus = 5, divineFavorBonus = 5)
        )
    )
}
