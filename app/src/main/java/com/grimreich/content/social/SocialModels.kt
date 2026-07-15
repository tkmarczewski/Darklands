package com.grimreich.content.social

enum class NpcRole {
    TAVERN_KEEPER, INNKEEPER, MERCHANT, PRIEST, GUARD, NOBLE, PEASANT
}

data class NamedNpc(
    val id: String,
    val name: String,
    val role: NpcRole,
    val cityId: String,
    val description: String = ""
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(cityId.isNotBlank()) { "City ID cannot be blank" }
    }
}

enum class SocialBackground {
    NOBILITY, URBAN_COMMONERS, RURAL_COMMONERS, CLERGY, ACADEMIC, MILITARY
}
