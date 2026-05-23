package com.darklandsmobile.core

data class AgingEffect(
    val strDelta: Int = 0,
    val agiDelta: Int = 0,
    val intDelta: Int = 0,
    val virtueDelta: Int = 0,
    val description: String = ""
)

object AgingSystem {

    private val AGE_EFFECTS: Map<IntRange, AgingEffect> = mapOf(
        (7..13) to AgingEffect(strDelta = 1, agiDelta = 1, description = "Dzieciństwo: wzrost siły i zwinności."),
        (14..20) to AgingEffect(strDelta = 2, agiDelta = 1, intDelta = 1, description = "Młodość: szybki rozwój."),
        (21..30) to AgingEffect(strDelta = 1, intDelta = 1, description = "Dojrzałość: stabilizacja."),
        (31..40) to AgingEffect(intDelta = 1, description = "Wiek średni: doświadczenie."),
        (41..50) to AgingEffect(strDelta = -1, description = "Pośredniość: początek degradacji."),
        (51..60) to AgingEffect(strDelta = -1, agiDelta = -1, intDelta = 1, description = "Starość: słabnie ciało, rośnie mądrość."),
        (61..99) to AgingEffect(strDelta = -2, agiDelta = -2, description = "Podesżły wiek: wyraźna degradacja.")
    )

    fun applyAging(hero: Hero, years: Int = 5): Pair<Hero, List<String>> {
        val messages = mutableListOf<String>()
        var updated = hero

        repeat(years) {
            updated = updated.copy(age = updated.age + 1)
        }

        val effect = AGE_EFFECTS.entries
            .firstOrNull { updated.age in it.key }
            ?.value

        if (effect != null) {
            updated = updated.copy(
                strength = maxOf(1, updated.strength + effect.strDelta),
                agility = maxOf(1, updated.agility + effect.agiDelta),
                intelligence = maxOf(1, updated.intelligence + effect.intDelta),
                virtue = maxOf(0, updated.virtue + effect.virtueDelta)
            )
            if (effect.description.isNotEmpty()) {
                messages.add(effect.description)
            }
        }

        // Efekt starzeń specjalnych
        if (updated.age >= 60 && updated.age % 10 == 0) {
            messages.add("Wiek ${updated.age}: ciało wyraźnie słabnie.")
        }

        return Pair(updated, messages)
    }

    fun ageDescription(age: Int): String = when (age) {
        in 7..13 -> "Dziecię"
        in 14..17 -> "Nastolatek"
        in 18..25 -> "Młodzieniec"
        in 26..35 -> "Dorosły"
        in 36..50 -> "Dojrzały"
        in 51..65 -> "Starzec"
        else -> "Podesżły"
    }

    fun isEligibleForCareerChange(hero: Hero): Boolean {
        return hero.age % 5 == 0
    }
}
