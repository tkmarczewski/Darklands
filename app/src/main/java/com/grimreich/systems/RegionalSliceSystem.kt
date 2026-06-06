package com.grimreich.systems

import com.grimreich.core.GrimholdSliceViewData
import com.grimreich.core.SliceQuestDetail
import com.grimreich.core.PlayerState

/**
 * Logic for the "Vertical Slice" screen.
 */
object RegionalSliceSystem {

    fun buildViewData(playerState: PlayerState): GrimholdSliceViewData {
        // Renamed Magdeburg references to Grimhold for 1.0 consistency
        val currentCity = playerState.currentCityId
        val displayCityName = if (currentCity == "grimhold") "Grimhold" else currentCity.uppercase()

        val activeQuests = QuestSystem.availableForCity(currentCity).take(3).map { q ->
            SliceQuestDetail(
                questId = q.id,
                title = q.title,
                rewardGold = q.rewardGold,
                difficultyLabel = "Hard",
                shortBrief = q.description
            )
        }

        return GrimholdSliceViewData(
            cityId = currentCity,
            cityTitle = displayCityName,
            backgroundUrl = "bg_grimhold_main",
            referenceTitle = displayCityName,
            sourceLabel = "GrimReich 1.0 Vertical Slice",
            moodText = "The air is heavy with the scent of old parchment and dry blood.",
            gold = playerState.gold,
            activeQuestId = playerState.activeQuestId,
            quests = activeQuests
        )
    }
}
