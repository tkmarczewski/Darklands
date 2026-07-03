package com.grimreich.core

data class SaveSnapshot(
    val version: Int,
    val timestamp: Long,
    val label: String,
    val state: GameState,
    val checksum: String? = null
)
