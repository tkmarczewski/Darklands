package com.grimreich.core.mutations

import kotlinx.serialization.Serializable

fun Mutation.toDto(): MutationDto = MutationDto(
    id = id,
    name = name,
    description = description,
    category = category.name,
    tier = tier.name,
    attributeModifiers = attributeModifiers,
    stabilityImpact = stabilityImpact,
    visualEffectId = visualEffectId
)

fun MutationDto.toDomain(): Mutation = Mutation(
    id = id,
    name = name,
    description = description,
    category = MutationCategory.valueOf(category),
    tier = MutationTier.valueOf(tier),
    attributeModifiers = attributeModifiers,
    stabilityImpact = stabilityImpact,
    visualEffectId = visualEffectId
)

@Serializable
data class MutationDto(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val tier: String,
    val attributeModifiers: Map<String, Int>,
    val stabilityImpact: Int,
    val visualEffectId: String?
)
