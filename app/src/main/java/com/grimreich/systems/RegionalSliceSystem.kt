package com.grimreich.systems

import com.grimreich.core.GrimholdSliceViewData
import com.grimreich.core.SliceQuestDetail
import com.grimreich.core.PlayerState
import com.grimreich.world.CityCatalogue

/**
 * Logic for the "Vertical Slice" screen.
 */
object RegionalSliceSystem {

    fun buildViewData(playerState: PlayerState): GrimholdSliceViewData {
        val currentCity = playerState.currentCityId
        val displayCityName = CityCatalogue.get(currentCity)?.name ?: currentCity.uppercase()

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
            sourceLabel = "GrimReich 1.5 Evolution Slice",
            moodText = "The air is heavy with the scent of old parchment and dry blood.",
            gold = playerState.gold,
            activeQuestId = playerState.activeQuestId,
            quests = activeQuests
        )
    }
}
