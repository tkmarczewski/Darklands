package com.grimreich.core

data class WorldState(
    var region: String = "town",
    var location: String = "Grimhold",
    var day: Int = 1,
    var timeOfDay: String = "morning",
    var fatigue: Int = 0,
    var lastEncounter: String = "none",
    var season: Season = Season.SPRING,
    var globalStability: Int = 100, // 0-100
    var weather: WeatherType = WeatherType.CLEAR,
    var echoIntensity: Float = 0.0f, // 0.0 - 1.0 (Era of Fracture)
    var collapseProgress: Float = 0.0f // 0.0 - 1.0 (Transition to 2.0)
)
