package com.grimreich.core

data class PersistentMeta(
    var anchorIdentity: String? = null,
    var totalSessionsFinished: Int = 0,
    val unlockedLegacyBuffs: MutableSet<String> = mutableSetOf(),
    var maxMetaAwarenessReached: Int = 0,
    
    // --- QUANTUM SCAN: Unity of Seven Selves ---
    // Tracks which aspects of the Anchor have been recognized/stabilized
    val unitedSelves: MutableSet<SelfAspect> = mutableSetOf()
) {
    enum class SelfAspect {
        WRATH, FEAR, HOPE, EMPTINESS, LIGHT, SHADOW, PEACE
    }
}
