package com.grimreich.systems

import com.grimreich.core.PlayerState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegionalSliceSystem @Inject constructor(
    private val questEngine: QuestEngine
) {
    fun buildViewData(playerState: PlayerState): GrimholdSliceViewData {
        return GrimholdSliceViewData(
            title = "Region: ${playerState.currentCityId}",
            description = "Eksploracja regionu w toku.",
            availableQuests = questEngine.getActiveQuestsForCity(playerState.currentCityId).map { it.title }
        )
    }
}
