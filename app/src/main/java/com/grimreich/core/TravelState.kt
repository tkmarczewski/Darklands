package com.grimreich.core

data class TravelState(
    val totalHoursTraveled: Int = 0,
    val fatigue: Int = 0,
    val lastEncounterId: String? = null
)