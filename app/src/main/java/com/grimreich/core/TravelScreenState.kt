package com.grimreich.core

data class TravelScreenState(
    val fromCityId: String,
    val toCityId: String,
    val totalHoursTraveled: Int,
    val fatigue: Int,
    val lastEncounterId: String?
)