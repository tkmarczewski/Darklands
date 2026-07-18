package com.grimreich.core.mutations

import kotlinx.serialization.Serializable

@Serializable
enum class MutationCategory {
    physical, mental, ontological, echo;

    companion object {
        @JvmField val PHYSICAL = physical
        @JvmField val MENTAL = mental
        @JvmField val ONTOLOGICAL = ontological
        @JvmField val ECHO = echo
    }
}

@Serializable
enum class MutationTier {
    dormant, manifested, dominant, transcendent;

    companion object {
        @JvmField val DORMANT = dormant
        @JvmField val MANIFESTED = manifested
        @JvmField val DOMINANT = dominant
        @JvmField val TRANSCENDENT = transcendent
    }
}

@Serializable
data class Mutation(
    val id: String,
    val name: String,
    val description: String,
    val category: MutationCategory,
    val tier: MutationTier = MutationTier.dormant,
    val attributeModifiers: Map<String, Int> = emptyMap(),
    val stabilityImpact: Int = 0,
    val visualEffectId: String? = null
)
