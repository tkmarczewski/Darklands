package com.grimreich.domain.history

data class Timeline(
    val id: String,
    val name: String,
    val isPrimary: Boolean,
    val events: List<String>
)

data class ParadoxState(
    val severity: Int,
    val affectedTimelines: List<String>
)
