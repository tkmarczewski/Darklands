package com.darklandsmobile.core

import com.darklandsmobile.systems.QuestEntry

data class CityScreenState(
    val cityId: String,
    val availableQuests: List<QuestEntry>,
    val gold: Int,
    val activeQuestId: String?
)