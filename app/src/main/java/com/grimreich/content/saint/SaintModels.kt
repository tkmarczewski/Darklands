package com.grimreich.content.saint

data class SaintPower(
    val name: String,
    val description: String,
    val faithCost: Int,
    val effect: String
) {
    init {
        require(faithCost > 0) { "Faith cost must be positive" }
    }
}

data class SaintData(
    val id: String,
    val name: String,
    val domain: String,
    val patronage: String,
    val power: SaintPower? = null
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
        require(name.isNotBlank()) { "Name cannot be blank" }
    }
}
