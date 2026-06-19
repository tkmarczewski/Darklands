package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.grimreich.v1.Item
import javax.inject.Inject
import javax.inject.Singleton

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

@Singleton
class QuestResolutionSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val questSystem: QuestSystem,
    private val lootSystem: LootSystem,
    private val reputationSystem: ReputationSystem
) {
    fun completeQuestWithRewards(
        questId: String,
        partyState: TravelPartyState? = null,
        faction: CityFaction = CityFaction.COMMONERS,
        reputationDelta: Int = 5
    ): QuestRewardResult {
        val completedQuest = questSystem.complete(questId)
        
        val updatedReputation = reputationSystem.modify(completedQuest.cityId, faction, reputationDelta)

        val updatedParty = partyState?.copy(
            lastEncounterId = "quest_complete:${completedQuest.id}"
        ) ?: TravelPartyState(lastEncounterId = "quest_complete:${completedQuest.id}")
        
        val items = mutableListOf<Item>()
        lootSystem.rollLoot(0.4f)?.let {
            items.add(it)
            gameRepository.currentState().inventory.add(it)
        }

        gameRepository.persistCurrentState()

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
