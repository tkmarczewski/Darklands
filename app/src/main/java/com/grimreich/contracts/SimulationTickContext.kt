package com.grimreich.contracts

/**
 * Scale of the simulation tick.
 * MICRO: Local interactions, frame-by-frame logic.
 * MESO: Region-wide updates, tactical shifts.
 * MACRO: Global world progression, historical turns.
 */
enum class SimulationScale { MICRO, MESO, MACRO }

/**
 * Contextual data for a single simulation tick.
 */
data class SimulationTickContext(
    val scale: SimulationScale,
    val deltaTime: Float,
    val worldSeed: Int,
    val currentDay: Int,
    val totalTicks: Long
)
