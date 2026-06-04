package com.darklandsmobile.content

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

data class EnemyStats(
    val hp: Int,
    val strength: Int,
    val agility: Int,
    val intellect: Int,
    val constitution: Int,
    val armor: Int
) {
    init {
        require(hp > 0) { "HP must be positive" }
        require(armor >= 0) { "Armor cannot be negative" }
    }
}

enum class EnemyCategory {
    HUMANOID, MONSTER, UNDEAD, ANIMAL, DEMON
}

data class EnemyType(
    val id: String,
    val name: String,
    val type: EnemyCategory,
    val baseStats: EnemyStats,
    val specialTraits: List<String> = emptyList()
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
    }
}

data class QuestEvent(
    val id: String,
    val description: String,
    val nextEventIds: List<String> = emptyList()
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
    }
}

data class QuestRewards(
    val gold: Int = 0,
    val virtue: Int = 0,
    val reputation: Int = 0
) {
    init {
        require(gold >= 0) { "Gold cannot be negative" }
    }
}

data class QuestEnding(
    val id: String,
    val description: String,
    val requirementEvents: List<String>
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
    }
}

data class QuestChain(
    val id: String,
    val name: String,
    val startingRegion: String,
    val events: List<QuestEvent>,
    val rewards: QuestRewards,
    val endings: List<QuestEnding>
) {
    init {
        require(events.isNotEmpty()) { "Events cannot be empty" }
        require(endings.isNotEmpty()) { "Endings cannot be empty" }
    }
}

enum class RumorSource {
    TAVERN, CHURCH, MARKET, NOBILITY, UNDERWORLD
}

data class Rumor(
    val id: String,
    val text: String,
    val region: String,
    val sourceType: RumorSource,
    val veracity: Float = 0.5f,
    val linkedQuestId: String? = null
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
        require(text.isNotBlank()) { "Text cannot be blank" }
        require(veracity in 0f..1f) { "Veracity must be between 0 and 1" }
    }
}

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
