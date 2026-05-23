package com.darklandsmobile.core

data class ReputationState(
    val city: MutableMap<String, Int> = mutableMapOf(
        "magdeburg" to 0, "cologne" to 0, "frankfurt" to 0
    ),
    val religious: Int = 0,
    val guild: MutableMap<String, Int> = mutableMapOf()
)
