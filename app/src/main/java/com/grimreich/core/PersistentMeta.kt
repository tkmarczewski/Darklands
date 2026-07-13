package com.grimreich.core

data class PersistentMeta(
    var anchorIdentity: String? = null,
    var totalSessionsFinished: Int = 0,
    val unlockedLegacyBuffs: MutableSet<String> = mutableSetOf(),
    var maxMetaAwarenessReached: Int = 0
)
