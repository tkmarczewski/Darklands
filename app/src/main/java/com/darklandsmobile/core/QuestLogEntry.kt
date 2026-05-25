package com.darklandsmobile.core

data class QuestLogEntry(
    val questId: String,
    val title: String,
    val status: String,
    val notes: String = ""
)