package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.world.CityCatalogue

object GrimholdSliceSystem {

    fun view(playerState: PlayerState): GrimholdSliceViewData {
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
            sourceLabel = "GrimReich 1.0 Vertical Slice",
            moodText = "The air is heavy with the scent of old parchment and dry blood.",
            gold = playerState.gold,
            activeQuestId = playerState.activeQuestId,
            quests = activeQuests
        )
    }
}
