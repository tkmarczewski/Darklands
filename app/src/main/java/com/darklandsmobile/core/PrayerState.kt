package com.darklandsmobile.core

data class PrayerState(
    var faith: Int = 50,
    var virtue: Int = 50,
    var blessings: Int = 0,
    var sins: Int = 0,
    var shrineVisited: Boolean = false
)
