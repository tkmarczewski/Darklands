package com.grimreich.world

import com.grimreich.grimreich.v1.NPC
import kotlin.random.Random

object ProceduralNpcGenerator {

    private val roles = listOf(
        "Chronicler", "Zealot", "Merchant", "Fugitive", "Mystic",
        "Gravedigger", "Penitent", "Heretic", "Soldier", "Orphan",
        "Seer", "Blacksmith", "Beggar", "Inquisitor", "Amnesiac"
    )

    fun generateForCity(cityId: String, seed: Int): List<NPC> {
        // Use dynamic seed based on current time to ensure different NPCs each visit
        val dynamicSeed = System.currentTimeMillis().toInt() + cityId.hashCode()
        val random = Random(dynamicSeed)
        val count = random.nextInt(4, 7)
        val generatedNames = mutableSetOf<String>()

        return (0 until count).mapNotNull { i ->
            var name = generateName(random)
            var attempts = 0
            while (generatedNames.contains(name) && (attempts < 10)) {
                name = generateName(random)
                attempts++
            }
            if (generatedNames.contains(name)) return@mapNotNull null
            generatedNames.add(name)

            val role = roles.random(random)
            NPC(
                id = "npc_${cityId}_${dynamicSeed}_$i",
                name = name,
                role = role,
                factionId = decideFaction(cityId, random),
                stability = 0.3f + (random.nextFloat() * 0.7f),
                startNodeId = "${role.lowercase()}_start",
            )
        }
    }

    fun generateName(random: Random = Random.Default): String {
        val prefixes = listOf(
            "Gisbert", "Helga", "Ulrich", "Mira", "Roderick", "Elsa", 
            "Balthazar", "Ingrid", "Sigmund", "Freya", "Klaus", "Martha",
            "Wilhelm", "Gerda", "Otto", "Beatrice", "Heinrich", "Lotte",
            "Siegfried", "Ursula", "Kaspar", "Greta", "Elias", "Anselm",
            "Ralwing", "Aldric", "Brunhilda", "Dieter", "Emmeline", "Friedrich",
            "Gunter", "Hilde", "Ignatz", "Jutta", "Konrad", "Lorelei"
        )
        val suffixes = listOf(
            "von Kalt", "the Broken", "of the Mist", "Soul-Stitched", "Grey",
            "the Silent", "Iron-Hand", "of the Void", "Parchment-Skin", "the Blind",
            "Shadow-Walker", "of Old Grimhold", "the Penitent", "Flesh-Weaver",
            "the Drowned", "of the Ash", "Twice-Born", "Void-Touched", "Sun-Eater",
            "the Cruel", "of the Black Rose", "the Forgiven", "Stone-Heart"
        )
        return "${prefixes.random(random)} ${suffixes.random(random)}"
    }

    private fun decideFaction(cityId: String, random: Random): String? {
        val city = CityCatalogue.get(cityId) ?: return null
        return if (random.nextFloat() < 0.7f) city.rulingFaction else "Commoners"
    }
}
