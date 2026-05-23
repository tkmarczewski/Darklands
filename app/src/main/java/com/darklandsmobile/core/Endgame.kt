package com.darklandsmobile.core

// ==================== ENDGAME QUEST CHAIN ====================

enum class EndgameQuestStatus {
    LOCKED, AVAILABLE, ACTIVE, COMPLETED, FAILED
}

data class EndgameQuestNode(
    val id: String,
    val title: String,
    val description: String,
    val minFaith: Int = 0,
    val minVirtue: Int = 0,
    val minCityReputation: Int = 0,
    val minFactionReputation: Map<String, Int> = emptyMap(),
    val requiredQuestIds: List<String> = emptyList(),
    val goldReward: Int = 0,
    val faithReward: Int = 0,
    val reputationReward: Int = 0,
    val divineFavorReward: Int = 0,
    var status: EndgameQuestStatus = EndgameQuestStatus.LOCKED
)

object EndgameQuestChain {

    val nodes = mutableListOf(
        EndgameQuestNode(
            id = "eq1_signs",
            title = "Znaki Skażenia",
            description = "Odkryj wpływy kultu w mieście, klasztorze i na trakcie.",
            minFaith = 3, minCityReputation = 2,
            goldReward = 50, faithReward = 2, reputationReward = 3
        ).also { it.status = EndgameQuestStatus.AVAILABLE },
        EndgameQuestNode(
            id = "eq2_alliances",
            title = "Złamane Sojusze",
            description = "Zdobyć wsparcie frakcji lub obejść wrogów.",
            minFactionReputation = mapOf("church" to 3, "military" to 2),
            requiredQuestIds = listOf("eq1_signs"),
            goldReward = 80, reputationReward = 5, divineFavorReward = 2
        ),
        EndgameQuestNode(
            id = "eq3_pilgrimage",
            title = "Pielgrzymka do Bramy",
            description = "Dotrzyj do ostatniego miejsca kultu przez wiarę i łaskę.",
            minFaith = 6, minVirtue = 4,
            requiredQuestIds = listOf("eq2_alliances"),
            goldReward = 120, faithReward = 5, divineFavorReward = 5
        )
    )

    fun findById(id: String) = nodes.firstOrNull { it.id == id }

    fun isAvailable(node: EndgameQuestNode, gameState: GameState,
                    factionRep: FactionReputationSystem,
                    completedIds: Set<String>): Boolean {
        val prayer = gameState.prayer
        return prayer.faith >= node.minFaith &&
            prayer.virtue >= node.minVirtue &&
            gameState.reputation.cityReputation >= node.minCityReputation &&
            node.requiredQuestIds.all { it in completedIds } &&
            node.minFactionReputation.all { (fid, minRep) ->
                factionRep.getReputation(fid) >= minRep
            }
    }

    fun completeNode(node: EndgameQuestNode, gameState: GameState): String {
        node.status = EndgameQuestStatus.COMPLETED
        gameState.world.gold += node.goldReward
        gameState.prayer.faith += node.faithReward
        gameState.reputation.cityReputation += node.reputationReward
        gameState.prayer.divineFavor += node.divineFavorReward

        // Odblokuj następny node
        nodes.forEach { next ->
            if (next.status == EndgameQuestStatus.LOCKED &&
                next.requiredQuestIds.contains(node.id)) {
                next.status = EndgameQuestStatus.AVAILABLE
            }
        }
        return "Ukończono: ${node.title}. Nagrody: +${node.goldReward} złota, +${node.faithReward} wiary."
    }

    fun allCompleted(): Boolean = nodes.all {
        it.status == EndgameQuestStatus.COMPLETED
    }
}

// ==================== ENDING SYSTEM ====================

enum class EndingType {
    GOOD, PRAGMATIC, CORRUPTED, REDEMPTION
}

data class Ending(
    val type: EndingType,
    val title: String,
    val description: String
)

object EndingSystem {

    fun resolveEnding(gameState: GameState): Ending {
        val prayer = gameState.prayer
        val faith = prayer.faith
        val virtue = prayer.virtue
        val sins = prayer.sins
        val divineFavor = prayer.divineFavor
        val cityRep = gameState.reputation.cityReputation

        return when {
            faith >= 8 && virtue >= 6 && cityRep >= 5 && sins <= 2 ->
                Ending(
                    EndingType.GOOD,
                    "Oczyszczenie",
                    "Twoja wiara i cnota przyniosły pokój. Świat odzyskał równowagę. " +
                    "Twoje imię zostało zapisane wśród świętych obrońców."
                )
            faith >= 5 && cityRep >= 3 && sins <= 5 ->
                Ending(
                    EndingType.PRAGMATIC,
                    "Gorzkie Zwycięstwo",
                    "Pokonałeś zagrożenie, ale świat pozostał poraniony. " +
                    "Historia zapamięta cię jako skutecznego, lecz nie czystego."
                )
            divineFavor >= 10 && sins >= 6 ->
                Ending(
                    EndingType.REDEMPTION,
                    "Odkupienie",
                    "Mimo ciężkich strat i grzechów, łaska boża nie opuściła cię. " +
                    "Twoje odkupienie jest prawdziwe i kosztowne."
                )
            else ->
                Ending(
                    EndingType.CORRUPTED,
                    "Skażenie",
                    "Zbyt wiele złych wyborów. Świat pochłonęło skażenie, " +
                    "a ty stałeś się częścią ciemności, którą chciałeś pokonać."
                )
        }
    }

    fun endingDescription(type: EndingType): String = when (type) {
        EndingType.GOOD -> "Zwycięstwo wiary i cnoty."
        EndingType.PRAGMATIC -> "Zwycięstwo pragmatyzmu."
        EndingType.REDEMPTION -> "Odkupienie przez łaskę."
        EndingType.CORRUPTED -> "Upadek w skażenie."
    }
}
