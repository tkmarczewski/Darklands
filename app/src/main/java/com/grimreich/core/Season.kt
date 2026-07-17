package com.grimreich.core

enum class Season {
    spring,
    summer,
    autumn,
    winter;

    fun displayName(): String = when (this) {
        spring -> "Wiosna"
        summer -> "Lato"
        autumn -> "Jesień"
        winter -> "Zima"
    }

    fun travelModifier(): Float = when (this) {
        summer -> 0.9f
        spring -> 1.0f
        autumn -> 1.1f
        winter -> 1.4f
    }
}

