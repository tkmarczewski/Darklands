package com.grimreich.core.mutations

object MutationRegistry {
    val allMutations = listOf(
        Mutation(
            id = "mut_mist_eyes",
            name = "Oczy Mgły",
            description = "Twoje źrenice rozmywają się, pozwalając dostrzec to, co ukryte w oparach.",
            category = MutationCategory.ONTOLOGICAL,
            attributeModifiers = mapOf("perception" to 2, "piety" to -1),
            stabilityImpact = -5
        ),
        Mutation(
            id = "mut_iron_skin",
            name = "Żelazna Skóra",
            description = "Twoja tkanka twardnieje, przypominając zimną stal Ferrum.",
            category = MutationCategory.PHYSICAL,
            attributeModifiers = mapOf("endurance" to 3, "agility" to -2),
            stabilityImpact = -10
        ),
        Mutation(
            id = "mut_echo_voice",
            name = "Głos Echa",
            description = "Mówisz wieloma głosami naraz, co budzi lęk i fascynację.",
            category = MutationCategory.ECHO,
            attributeModifiers = mapOf("charisma" to 2, "intelligence" to 1),
            stabilityImpact = -8
        ),
        Mutation(
            id = "mut_void_heart",
            name = "Serce Pustki",
            description = "Twoje tętno zwalnia do jednego uderzenia na godzinę. Strach przestaje istnieć.",
            category = MutationCategory.MENTAL,
            attributeModifiers = mapOf("strength" to 2, "charisma" to -3),
            stabilityImpact = -15
        )
    )
}
