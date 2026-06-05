package com.grimreich.core

import com.grimreich.systems.QuestEntry

data class CityScreenState(
    val cityId: String,
    val availableQuests: List<QuestEntry>,
    val gold: Int,
    val activeQuestId: String?
)