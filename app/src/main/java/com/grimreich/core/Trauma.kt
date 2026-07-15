package com.grimreich.core

/**
 * Definiuje traumę, która wpływa na statystyki i psychikę bohatera.
 */
data class Trauma(
    val id: String,
    val name: String,
    val description: String,
    val statModifiers: Map<String, Int>, // np. "attack" to -5, "defense" to 2
    val severity: Int // 1-3 (Lekka, Głęboka, Nieodwracalna)
)
