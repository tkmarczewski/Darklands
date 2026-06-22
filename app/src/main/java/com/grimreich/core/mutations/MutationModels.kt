package com.grimreich.core.mutations

enum class MutationCategory {
    PHYSICAL, MENTAL, ONTOLOGICAL, ECHO
}

enum class MutationTier {
    DORMANT, MANIFESTED, DOMINANT, TRANSCENDENT
}

data class Mutation(
    val id: String,
    val name: String,
    val description: String,
    val category: MutationCategory,
    val tier: MutationTier = MutationTier.DORMANT,
    val attributeModifiers: Map<String, Int> = emptyMap(),
    val stabilityImpact: Int = 0,
    val visualEffectId: String? = null
)
