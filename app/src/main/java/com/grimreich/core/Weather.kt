package com.grimreich.core

import kotlinx.serialization.Serializable

@Serializable
enum class WeatherType {
    clear, mist, blood_rain, eclipse, storm;

    companion object {
        @JvmField val CLEAR = clear
        @JvmField val MIST = mist
        @JvmField val BLOOD_RAIN = blood_rain
        @JvmField val ECLIPSE = eclipse
        @JvmField val STORM = storm
    }

    fun displayName(): String = when (this) {
        clear, CLEAR -> "Przejrzyście"
        mist, MIST -> "Mgła"
        blood_rain, BLOOD_RAIN -> "Krwawy deszcz"
        eclipse, ECLIPSE -> "Zaćmienie"
        storm, STORM -> "Burza"
    }
}
