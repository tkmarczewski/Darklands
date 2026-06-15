package com.grimreich.domain.region

import com.grimreich.domain.mutation.MutationEffect

data class RegionGeometry(
    val layers: List<String>, // e.g. "Mist", "Chaos", "Zero"
    val stability: Float
)

data class RegionAdvancedState(
    val id: String,
    val name: String,
    val geometry: RegionGeometry,
    val localMutations: List<MutationEffect>
)
