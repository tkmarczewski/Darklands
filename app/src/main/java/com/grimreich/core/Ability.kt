package com.grimreich.core

data class Ability(
    val id: String,
    val name: String,
    val description: String,
    val costType: CostType = CostType.NONE,
    val costValue: Int = 0
)

enum class CostType { NONE, PRAYER, HP, SANITY }

object AbilityRegistry {
    val SOLARIAN_STRIKE = Ability(
        id = "solarian_strike",
        name = "Uderzenie Solarianu",
        description = "Potężny atak zadający obrażenia zależne od Pobożności.",
        costType = CostType.PRAYER,
        costValue = 5
    )

    val SHADOW_VEIL = Ability(
        id = "shadow_veil",
        name = "Zasłona Cienia",
        description = "Zmniejsza szansę na bycie trafionym przez przeciwników.",
        costType = CostType.SANITY,
        costValue = 2
    )

    val IRON_SKIN = Ability(
        id = "iron_skin",
        name = "Żelazna Skóra",
        description = "Tymczasowo zwiększa Wytrzymałość.",
        costType = CostType.NONE
    )

    val HOLY_RAGE = Ability(
        id = "holy_rage",
        name = "Święta Furia",
        description = "Zwiększa Siłę w walce kosztem Pobożności.",
        costType = CostType.PRAYER,
        costValue = 10
    )

    val MIND_READ = Ability(
        id = "mind_read",
        name = "Czytanie w Myślach",
        description = "Pozwala zajrzeć w echa cudzej świadomości.",
        costType = CostType.SANITY,
        costValue = 5
    )

    fun all() = listOf(SOLARIAN_STRIKE, SHADOW_VEIL, IRON_SKIN, HOLY_RAGE, MIND_READ)
}
