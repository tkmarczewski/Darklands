package com.grimreich.core

data class ReputationState(
    val city: MutableMap<String, Int> = mutableMapOf()
)