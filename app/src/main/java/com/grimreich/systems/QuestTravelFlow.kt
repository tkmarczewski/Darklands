package com.grimreich.systems

import com.grimreich.core.TravelPartyState

/**
 * Lightweight glue for traveling to a quest city, then resolving the quest.
 */
object QuestTravelFlow {
    fun travelAndResolve(
        fromCityId: String,
        questId: String,
        partyState: TravelPartyState,
        faction: CityFaction = CityFaction.COMMONERS
    ): QuestRewardResult {
        val quest = QuestSystem.all().firstOrNull { it.id == questId }
            ?: error("Unknown quest: $questId")

        val active = QuestSystem.activate(quest.id)
        val traveledState = if (fromCityId != active.cityId) {
            TravelSystem.travel(fromCityId, active.cityId, partyState).first
        } else {
            partyState
        }

        return QuestResolutionSystem.completeQuestWithRewards(
            questId = active.id,
            partyState = traveledState,
            faction = faction,
            reputationDelta = 5
        )
    }
}
