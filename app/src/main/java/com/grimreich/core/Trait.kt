package com.grimreich.core

enum class Trait(val displayName: String, val description: String) {
    GIFT_OF_MIST("Dar Mgły", "Lepiej widzisz w oparach."),
    IRON_HEART("Żelazne Serce", "Zwiększona wytrzymałość fizyczna."),
    SOLAR_EYE("Oko Solara", "Wyostrzone zmysły i pobożność."),
    SHADOW_BORN("Zrodzony w Cieniu", "Zwinność okupiona mrokiem."),
    QUICK_HANDS("Szybkie Dłonie", "Twoje palce są zwinne jak echa."),
    NONE("Brak", "Zwykły śmiertelnik."),
    
    // Aliasy
    gift_of_mist("Dar Mgły", ""), iron_heart("Żelazne Serce", ""),
    solar_eye("Oko Solara", ""), shadow_born("Zrodzony w Cieniu", ""),
    quick_hands("Szybkie Dłonie", ""), none("Brak", "")
}

fun applyTraitModifiers(hero: Hero) {
    when (hero.trait) {
        Trait.IRON_HEART, Trait.iron_heart -> hero.maxHp += 5
        Trait.SOLAR_EYE, Trait.solar_eye -> hero.piety += 1
        Trait.QUICK_HANDS, Trait.quick_hands -> hero.agility += 1
        else -> Unit
    }
}
