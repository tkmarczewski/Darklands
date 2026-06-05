package com.grimreich.core

data class QuestState(
    val activeQuests: MutableList<String> = mutableListOf(),
    val completedQuests: MutableList<String> = mutableListOf(),
    val questProgress: MutableMap<String, Int> = mutableMapOf()
)
