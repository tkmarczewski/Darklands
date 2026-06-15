package com.grimreich.domain.absolute

enum class AbsoluteInterventionType {
    OVERRIDE,
    ERASE,
    CREATE,
    JUDGE
}

data class AbsoluteIntervention(
    val type: AbsoluteInterventionType,
    val targetId: String,
    val reason: String
)
