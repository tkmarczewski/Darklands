package com.grimreich.core

data class Ability(
    val id: String,
    val name: String,
    val description: String,
    val costType: CostType = CostType.none,
    val costValue: Int = 0
)

enum class CostType {
    none,
    prayer,
    hp,
    sanity,
    mana,
    stamina;

    companion object {
        @JvmField val NONE = none
        @JvmField val PRAYER = prayer
        @JvmField val HP = hp
        @JvmField val SANITY = sanity
        @JvmField val MANA = mana
        @JvmField val STAMINA = stamina
    }
}

object AbilityRegistry {
    val SOLARIAN_STRIKE = Ability(
        id = "solarian_strike",
        name = "Uderzenie Solarianu",
        description = "Potężny atak zadający obrażenia zależne od Pobożności.",
        costType = CostType.prayer,
        costValue = 5
    )

    val SHADOW_VEIL = Ability(
        id = "shadow_veil",
        name = "Zasłona Cienia",
        description = "Zmniejsza szansę na bycie trafionym przez przeciwników.",
        costType = CostType.sanity,
        costValue = 2
    )

    val IRON_SKIN = Ability(
        id = "iron_skin",
        name = "Żelazna Skóra",
        description = "Tymczasowo zwiększa Wytrzymałość.",
        costType = CostType.none
    )

    val HOLY_RAGE = Ability(
        id = "holy_rage",
        name = "Święta Furia",
        description = "Zwiększa Siłę w walce kosztem Pobożności.",
        costType = CostType.prayer,
        costValue = 10
    )

    val MIND_READ = Ability(
        id = "mind_read",
        name = "Czytanie w Myślach",
        description = "Pozwala zajrzeć w echa cudzej świadomości.",
        costType = CostType.sanity,
        costValue = 5
    )

    fun all() = listOf(SOLARIAN_STRIKE, SHADOW_VEIL, IRON_SKIN, HOLY_RAGE, MIND_READ)
}

fun canPayAbilityCost(hero: Hero, ability: Ability): Boolean = when (ability.costType) {
    CostType.mana -> true
    CostType.stamina -> hero.endurance >= ability.costValue
    CostType.sanity -> hero.sanity >= ability.costValue
    CostType.hp -> hero.hp > ability.costValue && hero.hp - ability.costValue >= 1
    CostType.none -> true
    CostType.prayer -> hero.piety >= ability.costValue
}

fun payAbilityCost(hero: Hero, ability: Ability) {
    when (ability.costType) {
        CostType.mana -> Unit
        CostType.stamina -> hero.endurance = (hero.endurance - ability.costValue).coerceAtLeast(0)
        CostType.sanity -> hero.sanity = (hero.sanity - ability.costValue).coerceAtLeast(0)
        CostType.hp -> hero.hp = (hero.hp - ability.costValue).coerceAtLeast(1)
        CostType.prayer -> hero.piety = (hero.piety - ability.costValue).coerceAtLeast(0)
        CostType.none -> Unit
    }
}

fun tryPayAbilityCost(hero: Hero, ability: Ability): Boolean {
    if (!canPayAbilityCost(hero, ability)) return false
    payAbilityCost(hero, ability)
    return true
}

fun refundAbilityCost(hero: Hero, ability: Ability) {
    when (ability.costType) {
        CostType.mana -> Unit
        CostType.stamina -> hero.endurance = (hero.endurance + ability.costValue).coerceAtMost(99)
        CostType.sanity -> hero.sanity = (hero.sanity + ability.costValue).coerceAtMost(100)
        CostType.hp -> hero.hp = (hero.hp + ability.costValue).coerceAtMost(hero.maxHp)
        CostType.prayer -> hero.piety = (hero.piety + ability.costValue).coerceAtMost(99)
        CostType.none -> Unit
    }
}
