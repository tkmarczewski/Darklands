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
        "Mieszczanin" to "end",
        "Pielgrzym" to "zealot_start",
        "Mistyk" to "mystic_start"
    )

    fun generateForCity(cityId: String, seed: Int): List<NPC> {
        val random = Random(seed.toLong())
        val count = 3 + random.nextInt(3)
        val state = gameRepository.currentState()
        
        val npcList = mutableListOf<NPC>()
        
        // Chance to spawn a "Crime Scene" for Verdict Chain if not started
        val hasVerdict1 = state.quest.activeQuests.contains("q_verdict_1") || state.quest.completedQuests.contains("q_verdict_1")
        if (!hasVerdict1 && random.nextInt(100) < 30) {
             npcList.add(NPC(
                id = "npc_verdict_hook",
                name = "Miejsce Zbrodni",
                role = "INCIDENT",
                startNodeId = "verdict_hook_start"
            ))
        }

        val roleKeys = roles.keys.toList()
        repeat(count) {
            val roleName = roleKeys[random.nextInt(roleKeys.size)]
            npcList.add(NPC(
                id = "npc_${cityId}_${it}",
                name = generateName(random),
                role = roleName,
                startNodeId = roles[roleName] ?: "end"
            ))
        }
        
        return npcList
    }

    private fun generateName(random: Random): String {
        val first = listOf("Klaus", "Hans", "Helga", "Greta", "Otto", "Bruno", "Marta", "Erich", "Ulrich", "Siegfried")
        val last = listOf("von Weber", "Schmidt", "Müller", "Wagner", "Becker", "Hoffmann", "Schulz", "Koch", "Bauer", "Richter")
        return "${first[random.nextInt(first.size)]} ${last[random.nextInt(last.size)]}"
    }
}
