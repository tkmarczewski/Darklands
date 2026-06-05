package com.grimreich.core

data class RegionalSliceViewData(
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
