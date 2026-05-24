package com.darklandsmobile.core

// Stan modlitwy bohatera trzymany w GameState. `favor` to mapa swiety -> laska (0..100),
// uzywana przez ReligionSystem i ekrany Saints/Prayer w testach sprintow 9+.
data class PrayerState(
    var faith: Int = 50,
    var virtue: Int = 50,
    var blessings: Int = 0,
    var sins: Int = 0,
    var shrineVisited: Boolean = false,
    val favor: MutableMap<String, Int> = mutableMapOf()
)
