package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

enum class AlchemyIngredient(val displayName: String, val basePrice: Int) {
    BRIMSTONE("Siarka", 15),
    MANGANES("Mangan", 12),
    SALT("Sól", 5)
}

enum class PotionQuality(val label: String, val priceMultiplier: Float) {
    LOW("Niska", 0.5f),
    MEDIUM("Średnia", 1.0f),
    HIGH("Wysoka", 2.0f)
}

@Singleton
class AlchemyCore @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun brew(hero: Hero, potionName: String): String {
        val alchSkill = hero.skills.getOrDefault("ALCH", 5)
        return if (alchSkill > 20) {
            gameRepository.log("${hero.name} uwarzył $potionName.")
            "Sukces!"
        } else {
            "Porażka."
        }
    }
}
