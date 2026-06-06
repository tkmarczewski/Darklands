package com.grimreich.core

data class PrayerState(
    var faith: Int = 10,       // Wiara (Uznanie przez Proroków)
    var virtue: Int = 50,      // Cnota (Stabilność Duszy)
    var sins: Int = 0,         // Grzechy (Skażenie)
    val blessings: MutableList<String> = mutableListOf() // Otrzymane Wizje
)
