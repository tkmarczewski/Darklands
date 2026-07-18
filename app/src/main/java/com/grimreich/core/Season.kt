package com.grimreich.core

import kotlinx.serialization.Serializable

@Serializable
enum class Season {
    spring, summer, autumn, winter;

    companion object {
        @JvmField val SPRING = spring
        @JvmField val SUMMER = summer
        @JvmField val AUTUMN = autumn
        @JvmField val WINTER = winter
    }

    fun displayName(): String = when (this) {
        spring, SPRING -> "Wiosna"
        summer, SUMMER -> "Lato"
        autumn, AUTUMN -> "Jesień"
        winter, WINTER -> "Zima"
    }

    fun travelModifier(): Float = when (this) {
        summer, SUMMER -> 0.9f
        spring, SPRING -> 1.0f
        autumn, AUTUMN -> 1.1f
        winter, WINTER -> 1.4f
    }
}
