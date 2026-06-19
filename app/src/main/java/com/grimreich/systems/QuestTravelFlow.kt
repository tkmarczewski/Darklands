package com.grimreich.systems

import com.grimreich.core.TravelPartyState
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lightweight glue for traveling to a quest city, then resolving the quest.
 */
@Singleton
class QuestTravelFlow @Inject constructor(
    private val questSystem: QuestSystem,
    private val questResolutionSystem: QuestResolutionSystem,
    private val travelSystem: TravelSystem
) {
    fun travelAndResolve(
        fromCityId: String,
        questId: String,
        partyState: TravelPartyState,
        faction: CityFaction = CityFaction.COMMONERS
    ): QuestRewardResult {
        val quest = questSystem.all().firstOrNull { it.id == questId }
            ?: error("Unknown quest: $questId")

        val active = questSystem.activate(quest.id)
        val traveledState = if (fromCityId != active.cityId) {
            travelSystem.travel(fromCityId, active.cityId, partyState).first
        } else {
            partyState
        }

        return questResolutionSystem.completeQuestWithRewards(
            questId = active.id,
            partyState = traveledState,
            faction = faction,
            reputationDelta = 5
        )
    }
}
