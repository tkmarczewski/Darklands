package com.grimreich.core

import com.grimreich.systems.AlchemySystem
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
        val alchSkill = hero.skills.getOrDefault("ALCH", 5)
        return when {
            alchSkill >= GrimConstants.Character.SPECIALIZED_SKILL_BASE_VALUE -> "Uwarzono mistrzowski eliksir."
            alchSkill >= 15 -> "Powstał solidny eliksir."
            alchSkill >= 8 -> "Powstała słaba, ale użyteczna mikstura."
            else -> "Mikstura nie wyszła."
        }
    }
}
