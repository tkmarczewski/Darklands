package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.ItemCatalogue
import kotlin.random.Random

data class QuestRewardResult(
    val questId: String,
    val goldAwarded: Int,
    val cityId: String,
    val affectedFaction: CityFaction,
    val reputationDelta: Int,
    val updatedReputation: Int,
    val updatedQuestStatus: QuestStatus,
    val updatedPartyState: TravelPartyState,
    val itemsAwarded: List<Item> = emptyList()
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
        
        // Random loot
        val items = mutableListOf<Item>()
        LootSystem.rollLoot(0.4f)?.let { 
            items.add(it)
            GameRepository.state.inventory.add(it)
        }

        return QuestRewardResult(
            questId = completedQuest.id,
            goldAwarded = completedQuest.rewardGold,
            cityId = completedQuest.cityId,
            affectedFaction = faction,
            reputationDelta = reputationDelta,
            updatedReputation = updatedReputation,
            updatedQuestStatus = completedQuest.status,
            updatedPartyState = updatedParty,
            itemsAwarded = items
        )
    }
}
