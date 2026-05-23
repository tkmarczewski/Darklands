package com.darklandsmobile.core

data class WorldState(
    var region: String = "town",
    var location: String = "Magdeburg",
    var day: Int = 1,
    var timeOfDay: String = "morning",
    var fatigue: Int = 0,
    var lastEncounter: String = "none",
    var season: Season = Season.SPRING
)
