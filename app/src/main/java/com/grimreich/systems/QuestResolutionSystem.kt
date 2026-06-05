package com.grimreich.systems

import com.grimreich.core.TravelPartyState

data class QuestRewardResult(
    val questId: String,
    val goldAwarded: Int,
    val cityId: String,
    val affectedFaction: CityFaction,
    val reputationDelta: Int,
    val updatedReputation: Int,
    val updatedQuestStatus: QuestStatus,
    val updatedPartyState: TravelPartyState
)

/**
 * Resolves quest completion into rewards, local reputation and optional travel progression.
 */
object QuestResolutionSystem {
    fun completeQuestWithRewards(
        questId: String,
        partyState: TravelPartyState,
        faction: CityFaction = CityFaction.COMMONERS,
        reputationDelta: Int = 5
    ): QuestRewardResult {
        val completedQuest = QuestSystem.complete(questId)
        val updatedReputation = ReputationSystem.modify(completedQuest.cityId, faction, reputationDelta)

        val updatedParty = partyState.copy(
            lastEncounterId = "quest_complete:${completedQuest.id}"
        )

        return QuestRewardResult(
            questId = completedQuest.id,
            goldAwarded = completedQuest.rewardGold,
            cityId = completedQuest.cityId,
            affectedFaction = faction,
            reputationDelta = reputationDelta,
            updatedReputation = updatedReputation,
            updatedQuestStatus = completedQuest.status,
            updatedPartyState = updatedParty
        )
    }
}
