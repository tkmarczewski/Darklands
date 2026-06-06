package com.grimreich.core

data class WorldState(
    var region: String = "town",
    var location: String = "Grimhold",
    var day: Int = 1,
    var timeOfDay: String = "morning",
    var fatigue: Int = 0,
    var lastEncounter: String = "none",
    var season: Season = Season.SPRING,
    var globalStability: Int = 100,
    var weather: WeatherType = WeatherType.CLEAR
)
