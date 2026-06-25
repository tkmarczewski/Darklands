package com.grimreich.world

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.NPC
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProceduralNpcGenerator @Inject constructor(
    private val gameRepository: GameRepository
) {
    private val roles = mapOf(
        "Kupiec" to "merchant_start",
        "Żebrak" to "beggar_start",
        "Strażnik" to "guard_start",
        "Alchemik" to "alchemist_start",
        "Mieszczanin" to "citizen_start",
        "Pielgrzym" to "zealot_start",
        "Mistyk" to "mystic_start"
    )

    fun generateForCity(cityId: String, seed: Int): List<NPC> {
        val state = gameRepository.currentState()
        
        // Return known NPCs if already generated for this city
        state.knownNpcs[cityId]?.let { return it }

        val random = Random(seed.toLong())
        val npcList = mutableListOf<NPC>()

        // 1. REGIONAL HEROES
        when (cityId) {
            "wybrzeze_polnocne" -> npcList.add(NPC(
                id = "hero_aelion",
                name = "Prorok Aelion",
                role = "MYSTIC",
                startNodeId = "aelion_start",
                isRegionalHero = true
            ))
            "rowniny_koronne" -> npcList.add(NPC(
                id = "hero_xyrel",
                name = "Inkwizytor Xyrel",
                role = "GUARD",
                startNodeId = "xyrel_start",
                isRegionalHero = true
            ))
        }

        // 2. INCIDENTS
        val hasVerdict1 = state.quest.activeQuests.contains("q_verdict_1") || state.quest.completedQuests.contains("q_verdict_1")
        if (!hasVerdict1 && random.nextInt(100) < 30) {
             npcList.add(NPC(
                id = "npc_verdict_hook",
                name = "Miejsce Zbrodni",
                role = "INCIDENT",
                startNodeId = "verdict_hook_start"
            ))
        }

        // 3. GENERATED NPCS
        val count = 2 + random.nextInt(3)
        val roleKeys = roles.keys.toList()
        repeat(count) {
            val roleName = roleKeys[random.nextInt(roleKeys.size)]
            val isInfested = state.world.globalStability < 40 && random.nextInt(100) < (50 - state.world.globalStability)
            
            npcList.add(NPC(
                id = "npc_${cityId}_${it}_${random.nextInt(1000)}",
                name = generateName(random),
                role = roleName,
                startNodeId = if (isInfested) "infested_start" else roles[roleName] ?: "end",
                isInfested = isInfested
            ))
        }
        
        // Persist the generated NPCs
        state.knownNpcs[cityId] = npcList
        gameRepository.persistCurrentState()
        
        return npcList
    }

    private fun generateName(random: Random): String {
        val first = listOf("Klaus", "Hans", "Helga", "Greta", "Otto", "Bruno", "Marta", "Erich", "Ulrich", "Siegfried")
        val last = listOf("von Weber", "Schmidt", "Müller", "Wagner", "Becker", "Hoffmann", "Schulz", "Koch", "Bauer", "Richter")
        return "${first[random.nextInt(first.size)]} ${last[random.nextInt(last.size)]}"
    }
}
