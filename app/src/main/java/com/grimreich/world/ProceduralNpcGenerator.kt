package com.grimreich.world

import com.grimreich.core.GameState
import com.grimreich.grimreich.v1.NPC
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ProceduralNpcGenerator @Inject constructor(
    private val echoSystem: com.grimreich.core.EchoSystem
) {

    fun generateForCity(cityId: String, state: GameState): List<NPC> {
        val random = Random(cityId.hashCode().toLong() + state.world.day.toLong())
        val npcList = mutableListOf<NPC>()
        
        val worldStability = state.world.globalStability
        val isGrim20 = worldStability < 35

        // 1. REGIONAL HEROES
        // ... (existing logic)
        
        // --- ADD ECHO SPAWN ---
        if (random.nextFloat() < 0.3f) {
            echoSystem.getRandomEcho()?.let { echo ->
                npcList.add(NPC(
                    id = "echo_${echo.id}",
                    name = "ECHO_${echo.name.uppercase().replace(" ", "_")}",
                    role = "ECHO",
                    startNodeId = "echo_start",
                    stability = 0.1f,
                    isInfested = true
                ))
            }
        }
        if (cityId == "wybrzeze_polnocne") {
            npcList.add(NPC(
                id = "aelion",
                name = if (isGrim20) "PROCES_AEL_ALPHA" else "Prorok Aelion",
                role = "AELION",
                isRegionalHero = true,
                startNodeId = "aelion_start",
                stability = (worldStability / 100f).coerceIn(0.1f, 1.0f)
            ))
        }
        if (cityId == "serce_krainy") {
            npcList.add(NPC(
                id = "mira",
                name = if (isGrim20) "SĘDZIA_MIRA_v2" else "Mira Wieloznaczna",
                role = "MIRA",
                isRegionalHero = true,
                startNodeId = "mira_start",
                stability = (worldStability / 100f).coerceIn(0.1f, 1.0f)
            ))
        }

        // 2. CANONICAL ROLES
        val roles = listOf("Merchant", "Guard", "Mystic", "Beggar")
        roles.forEach { role ->
            // FIX: Guaranteed spawn for starting city or if stability is high
            val guaranteed = cityId == "wybrzeze_polnocne" && (role == "Guard" || role == "Merchant")
            if (guaranteed || random.nextBoolean()) {
                val npcName = if (isGrim20) {
                    "INSTANCJA_${role.uppercase().take(3)}_${(100..999).random(random)}"
                } else {
                    generateName(role, random)
                }

                val infestationChance = if (worldStability < 20) 0.4f else if (worldStability < 50) 0.1f else 0.01f
                val isInfested = random.nextFloat() < infestationChance

                npcList.add(NPC(
                    id = "npc_${role.lowercase()}_$cityId",
                    name = npcName,
                    role = role,
                    startNodeId = "${role.lowercase()}_start",
                    stability = (worldStability / 100f).coerceIn(0.05f, 1.0f),
                    isInfested = isInfested
                ))
            }
        }

        return npcList
    }

    private fun generateName(role: String, random: Random): String {
        val names = listOf("Aldous", "Vane", "Kael", "Mina", "Garrick", "Liora", "Thane", "Elowen")
        return names.random(random) + " ($role)"
    }
}
