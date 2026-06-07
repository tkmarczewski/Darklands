package com.grimreich.world

import com.grimreich.grimreich.v1.NPC
import kotlin.random.Random

object ProceduralNpcGenerator {
    
    fun generateForCity(cityId: String, seed: Int): List<NPC> {
        val random = Random(seed + cityId.hashCode())
        val count = random.nextInt(2, 5)
        
        return (0 until count).map { i ->
            val role = listOf("Chronicler", "Zealot", "Merchant", "Fugitive", "Mystic").random(random)
            val name = generateName(random)
            NPC(
                id = "npc_${cityId}_$i",
                name = name,
                role = role,
                factionId = decideFaction(cityId, random),
                stability = 0.5f + random.nextFloat() * 0.5f,
                startNodeId = "${role.lowercase()}_start"
            )
        }
    }

    private fun generateName(random: Random): String {
        val prefixes = listOf("Gisbert", "Helga", "Ulrich", "Mira", "Roderick", "Elsa")
        val suffixes = listOf("von Kalt", "the Broken", "of the Mist", "Soul-Stitched", "Grey")
        return "${prefixes.random(random)} ${suffixes.random(random)}"
    }

    private fun decideFaction(cityId: String, random: Random): String? {
        val city = CityCatalogue.get(cityId) ?: return null
        return if (random.nextFloat() < 0.7f) city.rulingFaction else "Commoners"
    }
}
