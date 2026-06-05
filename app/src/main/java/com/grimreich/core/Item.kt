package com.grimreich.core

data class Item(
    val id: String,
    val name: String,
    val type: String,
    val slot: String? = null,
    val value: Int = 0,
    val weight: Double = 0.0,
    val effects: Map<String, Int> = emptyMap()
)
