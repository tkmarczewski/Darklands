package com.grimreich.contracts

import com.grimreich.core.GameState

enum class SimulationScale { micro, meso, macro }

data class SimulationTickContext(
    val scale: SimulationScale,
    val deltaHours: Int,
    val state: GameState
)
