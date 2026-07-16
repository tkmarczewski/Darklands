package com.grimreich.core

enum class Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;

    fun displayName(): String = when (this) {
        SPRING -> "Wiosna"
        SUMMER -> "Lato"
        AUTUMN -> "Jesień"
        WINTER -> "Zima"
    }

    fun travelModifier(): Float = when (this) {
        SUMMER -> 0.9f
        SPRING -> 1.0f
        AUTUMN -> 1.1f
        WINTER -> 1.4f
    }
}

