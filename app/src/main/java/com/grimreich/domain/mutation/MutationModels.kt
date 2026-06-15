package com.grimreich.domain.mutation

enum class MutationCategory {
    PHYSICAL,
    EMOTIONAL,
    MEMORY,
    IDENTITY,
    HISTORICAL,
    ABSOLUTE
}

data class MutationEffect(
    val id: String,
    val category: MutationCategory,
    val description: String,
    val intensity: Int // 1 to 10
)
