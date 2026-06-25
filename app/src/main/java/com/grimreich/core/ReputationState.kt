package com.grimreich.core

data class ReputationState(
    /**
     * Map of City ID to a Map of Faction (as String) to Score.
     * This replaces the flattened city score to support the full ReputationSystem.
     */
    val cityFactions: MutableMap<String, MutableMap<String, Int>> = mutableMapOf(),
    /**
     * Global faction standings that transcend individual cities.
     */
    val globalFactions: MutableMap<String, Int> = mutableMapOf()
)
