package com.grimreich.world

import com.grimreich.core.GameState
import com.grimreich.grimreich.v1.NPC
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ProceduralNpcGenerator @Inject constructor() {

    fun generateForCity(cityId: String, state: GameState): List<NPC> {
        val random = Random(cityId.hashCode() + state.world.day)
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
        if (cityId == "rowniny_koronne") {
             npcList.add(NPC(
                id = "xyrel",
                name = "Inkwizytor Xyrel",
                role = "XYREL",
                isRegionalHero = true,
                startNodeId = "xyrel_start"
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
        if (cityId == "gory_poludniowe") {
            npcList.add(NPC(
                id = "ferrun",
                name = "Ferrun Żelazny",
                role = "FERRUN",
                isRegionalHero = true,
                startNodeId = "ferrun_start"
            ))
        }
        if (cityId == "pogranicze_stepowe") {
            npcList.add(NPC(
                id = "noctyros",
                name = "Noctyros",
                role = "NOCTYROS",
                isRegionalHero = true,
                startNodeId = "noctyros_start"
            ))
        }

        // 2. CANONICAL ROLES
        val roles = listOf("Merchant", "Guard", "Zealot", "Mystic", "Beggar")
        roles.forEach { role ->
            if (random.nextBoolean()) {
                val isInfested = state.world.globalStability < 40 && random.nextInt(100) < 20
                val isGrim20 = state.world.globalStability < 35
                val personality = listOf("Normal", "Fanatic", "Weary", "Greedy").random(random)
                
                val npcName = if (isGrim20) {
                    "INSTANCJA_${role.uppercase().take(3)}_${(100..999).random(random)}"
                } else {
                    generateName(role, random)
                }

                npcList.add(NPC(
                    id = "npc_${role.lowercase()}_$cityId",
                    name = npcName,
                    role = role,
                    personality = personality,
                    isInfested = isInfested,
                    startNodeId = if (isInfested) "infested_start" else "${role.lowercase()}_${personality.lowercase()}_start"
                ))
            }
        }

        // 3. INCIDENTS (VERDICT HOOKS)
        val hasVerdict1 = state.quest.activeQuests.contains("q_verdict_1") || state.quest.completedQuests.contains("q_verdict_1")
        if (!hasVerdict1 && state.world.verdictIncidentsSeen < 3 && random.nextInt(100) < 50) {
             npcList.add(NPC(
                id = "npc_verdict_hook_${state.world.verdictIncidentsSeen}",
                name = "Miejsce Zbrodni",
                role = "INCIDENT",
                startNodeId = "verdict_hook_start"
            ))
        }

        // 4. DATA GHOSTS
        if (state.world.globalStability < 30 && random.nextInt(100) < 30) {
            npcList.add(NPC(
                id = "npc_ghost_$cityId",
                name = "Duch Danych",
                role = "INCIDENT",
                startNodeId = "data_ghost_start"
            ))
        }

        return npcList
    }

    private fun generateName(role: String, random: Random): String {
        val first = listOf("Siegfried", "Marta", "Erich", "Helga", "Kurt", "Klara")
        val last = listOf("Richter", "Maier", "Weber", "Wagner", "Schulz", "Hoffmann")
        return "${first.random(random)} ${last.random(random)}"
    }
}
