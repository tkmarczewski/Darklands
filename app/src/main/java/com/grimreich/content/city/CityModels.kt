package com.grimreich.content.city

enum class CityType {
    CITY, METROPOLIS, PORT, CAPITAL, BORDER_CITY
}

data class CityData(
    val id: String,
    val name: String,
    val region: String,
    val type: CityType,
    val population: Int,
    val priceModifier: Float = 1.0f
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(population > 0) { "Population must be positive" }
    }
}
