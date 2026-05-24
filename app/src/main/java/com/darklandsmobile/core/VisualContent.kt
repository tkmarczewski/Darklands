package com.darklandsmobile.core

data class CityVisual(
    val cityId: String,
    val title: String,
    val backdropName: String,
    val emblemName: String,
    val moodText: String
)

data class QuestCardViewData(
    val questId: String,
    val title: String,
    val cityId: String,
    val rewardGold: Int,
    val status: String,
    val accentLabel: String,
    val flavorText: String
)

data class CityHubViewData(
    val cityId: String,
    val cityTitle: String,
    val gold: Int,
    val activeQuestId: String?,
    val moodText: String,
    val backdropName: String,
    val emblemName: String,
    val questCards: List<QuestCardViewData>
)
