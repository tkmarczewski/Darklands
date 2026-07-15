package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

enum class AlchemyIngredient(val displayName: String, val basePrice: Int) {
    BRIMSTONE("Siarka", 10),
    MANGANES("Mangan", 12),
    SALT("Sól", 5)
}

enum class PotionQuality(val label: String, val priceMultiplier: Float) {
    LOW("Słaba", 0.5f),
    MEDIUM("Solidna", 1.0f),
    HIGH("Mistrzowska", 2.0f)
}

@Singleton
class AlchemyCore @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun brew(hero: Hero): String {
        hero.normalize()
        val alchSkill = hero.skills.getOrDefault("ALCH", 5)
        val result = when {
            alchSkill >= GameConstants.Character.SPECIALIZED_SKILL_BASE_VALUE -> "Uwarzono mistrzowski eliksir."
            alchSkill >= 15 -> "Powstał solidny eliksir."
            alchSkill >= 8 -> "Powstała słaba, ale użyteczna mikstura."
            else -> {
                if (hero.endurance > 0) hero.endurance = (hero.endurance - 1).coerceAtLeast(0)
                "Mikstura nie wyszła. (-1 Endurance)"
            }
        }
        hero.normalize()
        return result
    }
}
