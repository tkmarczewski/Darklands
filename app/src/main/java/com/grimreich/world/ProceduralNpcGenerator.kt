package com.grimreich.world

import com.grimreich.grimreich.v1.NPC
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProceduralNpcGenerator @Inject constructor(
    private val cityCatalogue: CityCatalogue
) {
    private val roles = listOf("Kupiec", "Żebrak", "Strażnik", "Alchemik", "Mieszczanin", "Pielgrzym")

    fun generateForCity(cityId: String, seed: Int): List<NPC> {
        val random = Random(seed.toLong())
        val count = 2 + random.nextInt(4)
        
        return List(count) {
            val role = roles[random.nextInt(roles.size)]
            NPC(
                id = "npc_${cityId}_${it}",
                name = generateName(random),
                role = role,
                startNodeId = "end"
            )
        }
    }

    private fun generateName(random: Random): String {
        val first = listOf("Klaus", "Hans", "Helga", "Greta", "Otto", "Bruno", "Marta", "Erich")
        val last = listOf("von Weber", "Schmidt", "Müller", "Wagner", "Becker", "Hoffmann")
        return "${first[random.nextInt(first.size)]} ${last[random.nextInt(last.size)]}"
    }
}
