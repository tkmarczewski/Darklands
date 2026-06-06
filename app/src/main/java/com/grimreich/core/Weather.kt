package com.grimreich.core

enum class WeatherType {
    CLEAR, MIST, BLOOD_RAIN, ECLIPSE, STORM;

    fun displayName(): String = when (this) {
        CLEAR -> "Przejrzyście"
        MIST -> "Mgła"
        BLOOD_RAIN -> "Krwawy deszcz"
        ECLIPSE -> "Zaćmienie"
        STORM -> "Burza"
    }
}
