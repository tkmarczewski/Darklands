package com.grimreich.core

enum class WeatherType {
    clear, mist, blood_rain, eclipse, storm;

    fun displayName(): String = when (this) {
        clear -> "Przejrzyście"
        mist -> "Mgła"
        blood_rain -> "Krwawy deszcz"
        eclipse -> "Zaćmienie"
        storm -> "Burza"
    }
}

