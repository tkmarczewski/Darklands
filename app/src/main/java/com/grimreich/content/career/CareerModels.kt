package com.grimreich.content.career

enum class CareerGroup {
    MILITARY, RELIGIOUS, ACADEMIC, URBAN, UNDERWORLD, RURAL
}

data class CareerRequirements(
    val minAge: Int = 0,
    val minStrength: Int = 0,
    val previousCareers: List<String> = emptyList()
)

data class CareerEffects(
    val attributeBonuses: Map<String, Int> = emptyMap(),
    val skillBonuses: Map<String, Int> = emptyMap()
)

data class CareerData(
    val id: String,
    val name: String,
    val group: CareerGroup,
    val description: String,
    val effects: CareerEffects,
    val requirements: CareerRequirements? = null
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
        require(name.isNotBlank()) { "Name cannot be blank" }
    }
}
