package com.grimreich.core

import kotlinx.serialization.Serializable

@Serializable
data class PersistentMeta(
    var anchorIdentity: String? = null,
    var totalSessionsFinished: Int = 0,
    var maxMetaAwarenessReached: Int = 0,
    val unlockedLegacyBuffs: MutableSet<String> = mutableSetOf(),
    val unitedSelves: MutableList<SelfAspect> = mutableListOf()
) {
    @Serializable
    enum class SelfAspect {
        fear, wrath, shadow, light, hope, emptiness, peace, anger, wisdom, empathy;

        companion object {
            @JvmField val FEAR = fear
            @JvmField val WRATH = wrath
            @JvmField val SHADOW = shadow
            @JvmField val LIGHT = light
            @JvmField val HOPE = hope
            @JvmField val EMPTINESS = emptiness
            @JvmField val PEACE = peace
            @JvmField val ANGER = anger
            @JvmField val WISDOM = wisdom
            @JvmField val EMPATHY = empathy
        }
    }
}
