package com.grimreich.core

data class SliceArtwork(
    val cityId: String,
    val backgroundUrl: String,
    val referenceTitle: String,
    val sourceLabel: String
)

data class SliceQuestDetail(
    val questId: String,
    val title: String,
    val shortBrief: String,
    val rewardGold: Int,
    val difficultyLabel: String
)

data class GrimholdSliceViewData(
    val cityId: String,
    val cityTitle: String,
    val moodText: String,
    val backgroundUrl: String,
    val referenceTitle: String,
    val sourceLabel: String,
    val gold: Int,
    val activeQuestId: String?,
    val quests: List<SliceQuestDetail>
)
