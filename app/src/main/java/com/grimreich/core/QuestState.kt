package com.grimreich.core

data class QuestState(
    val activeQuests: MutableList<String> = mutableListOf(),
    val completedQuests: MutableList<String> = mutableListOf(),
    val objectivesReached: MutableSet<String> = mutableSetOf(),
    val questProgress: MutableMap<String, Int> = mutableMapOf(),
    // Endgame (main) quest tracking
    val activeEndgameQuests: MutableList<String> = mutableListOf(),
    val completedEndgameQuests: MutableList<String> = mutableListOf()
)
