package com.grimreich.world

import com.grimreich.core.GameState
import com.grimreich.grimreich.v1.NPC
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ProceduralNpcGenerator @Inject constructor() {

    fun generateForCity(cityId: String, state: GameState): List<NPC> {
        val random = Random(cityId.hashCode().toLong() + state.world.day.toLong())
        val npcList = mutableListOf<NPC>()

        // 1. REGIONAL HEROES
        if (cityId == "wybrzeze_polnocne") {
            npcList.add(NPC(
                id = "aelion",
                name = "Prorok Aelion",
                role = "AELION",
                isRegionalHero = true,
                startNodeId = "aelion_start"
            ))
        }
        if (cityId == "serce_krainy") {
            npcList.add(NPC(
                id = "mira",
                name = "Mira Wieloznaczna",
                role = "MIRA",
                isRegionalHero = true,
                startNodeId = "mira_start"
            ))
        }

        // 2. CANONICAL ROLES
        val roles = listOf("Merchant", "Guard", "Mystic", "Beggar")
        roles.forEach { role ->
            if (random.nextBoolean()) {
                val isGrim20 = state.world.globalStability < 35
                
                val npcName = if (isGrim20) {
                    "INSTANCJA_${role.uppercase().take(3)}_${(100..999).random(random)}"
                } else {
                    generateName(role, random)
                }

                npcList.add(NPC(
                    id = "npc_${role.lowercase()}_$cityId",
                    name = npcName,
                    role = role,
                    startNodeId = "${role.lowercase()}_start"
                ))
            }
        }

        return npcList
    }

    private fun generateName(role: String, random: Random): String {
        val names = listOf("Aldous", "Vane", "Kael", "Mina", "Garrick", "Liora")
        return names.random(random) + " ($role)"
    }
}
