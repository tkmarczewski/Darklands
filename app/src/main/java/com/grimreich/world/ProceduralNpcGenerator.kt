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
        val random = Random(seed + cityId.hashCode())
        val count = random.nextInt(4, 7) // Zwiększono liczbę NPC
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
                id = "npc_${cityId}_$i",
                name = name,
                role = role,
                factionId = decideFaction(cityId, random),
                stability = 0.3f + (random.nextFloat() * 0.7f),
                startNodeId = "${role.lowercase()}_start",
            )
        }
    }

    private fun generateName(random: Random): String {
        val prefixes = listOf(
            "Gisbert", "Helga", "Ulrich", "Mira", "Roderick", "Elsa", 
            "Balthazar", "Ingrid", "Sigmund", "Freya", "Klaus", "Martha",
            "Wilhelm", "Gerda", "Otto", "Beatrice", "Heinrich", "Lotte",
            "Siegfried", "Ursula", "Kaspar", "Greta", "Elias", "Anselm"
        )
        val suffixes = listOf(
            "von Kalt", "the Broken", "of the Mist", "Soul-Stitched", "Grey",
            "the Silent", "Iron-Hand", "of the Void", "Parchment-Skin", "the Blind",
            "Shadow-Walker", "of Old Grimhold", "the Penitent", "Flesh-Weaver",
            "the Drowned", "of the Ash", "Twice-Born", "Void-Touched", "Sun-Eater"
        )
        return "${prefixes.random(random)} ${suffixes.random(random)}"
    }

    private fun decideFaction(cityId: String, random: Random): String? {
        val city = CityCatalogue.get(cityId) ?: return null
        return if (random.nextFloat() < 0.7f) city.rulingFaction else "Commoners"
    }
}
