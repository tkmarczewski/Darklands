package com.grimreich.systems

import com.grimreich.core.PlayerState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegionalSliceSystem @Inject constructor(
    private val questSystem: QuestSystem
) {
    fun buildViewData(playerState: PlayerState): GrimholdSliceViewData {
        val quests = questSystem.availableForCity(playerState.currentCityId)
        
        return GrimholdSliceViewData(
            title = "Region: ${playerState.currentCityId}",
            description = "Tereny poza murami Grimhold.",
            availableQuests = quests.map { it.title }
        )
    }
}
