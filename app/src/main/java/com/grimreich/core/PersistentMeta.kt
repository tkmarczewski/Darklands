package com.grimreich.core

data class PersistentMeta(
    var totalSessionsFinished: Int = 0,
    val unlockedLegacyBuffs: MutableSet<String> = mutableSetOf(),
    var maxMetaAwarenessReached: Int = 0
)
