package com.darklandsmobile.core

// Stan reputacji druzyny: per miasto, per frakcja, plus prosty wskaznik religijny i cechowy.
data class ReputationState(
    val city: MutableMap<String, Int> = mutableMapOf(
        "magdeburg" to 0, "cologne" to 0, "frankfurt" to 0
    ),
    val faction: MutableMap<String, Int> = mutableMapOf(),
    val guild: MutableMap<String, Int> = mutableMapOf(),
    var religious: Int = 0
)
