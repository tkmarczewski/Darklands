package com.darklandsmobile.core

data class ReputationState(
    val city: MutableMap<String, Int> = mutableMapOf()
)