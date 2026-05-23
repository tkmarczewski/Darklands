package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.WorldMap

object TravelSystem {
    fun travelTo(regionOrNodeId: String): String {
        val w = GameRepository.state.world
        val node = WorldMap.all().firstOrNull { it.id == regionOrNodeId || it.region == regionOrNodeId }
            ?: return "Nieznane miejsce: $regionOrNodeId"
        w.region    = node.region
        w.location  = node.name
        w.day      += 1
        w.fatigue  += 5
        w.timeOfDay = when (w.day % 3) { 0 -> "night"; 1 -> "morning"; else -> "afternoon" }
        val encounter = when (node.region) {
            "forest" -> randomEncounter()
            "road"   -> if ((0..2).random() == 0) randomEncounter() else "none"
            else     -> "none"
        }
        w.lastEncounter = encounter
        val msg = "Podroz do ${node.name} (dzien ${w.day})." +
            if (encounter != "none") " Spotkanie: $encounter!" else ""
        GameRepository.log(msg)
        return msg
    }
    private fun randomEncounter() =
        listOf("bandyci","pielgrzym","kupiec","wilki","rycerz-wloczykij").random()
}
