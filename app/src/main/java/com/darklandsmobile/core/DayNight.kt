package com.darklandsmobile.core

enum class TimeOfDay(val hour: Int) {
    MORNING(6),
    MIDDAY(12),
    AFTERNOON(15),
    DUSK(18),
    EVENING(21),
    MIDNIGHT(0),
    DEEP_NIGHT(3);

    fun isNight(): Boolean = this == EVENING || this == MIDNIGHT || this == DEEP_NIGHT
    fun isDusk(): Boolean = this == DUSK
}

data class DayNightState(
    var hour: Int = 12,
    var daysPassed: Int = 0
)

object DayNightSystem {
    fun advanceHours(state: DayNightState, hours: Int) {
        val totalHours = state.hour + hours
        state.hour = totalHours % 24
        state.daysPassed += totalHours / 24
    }

    fun encounterChanceModifier(time: TimeOfDay): Float = if (time.isNight()) 1.8f else 1.0f

    fun fatigueMod(time: TimeOfDay): Float = if (time.isNight()) 1.5f else 1.0f
}
