package com.grimreich.core

import com.grimreich.grimreich.v1.*

@Deprecated("Seeding logic moved to GameRepository.sync(). Scheduled for deletion.")
object GrimSeed {
    fun initialize() {
        val regions = GrimRegionCatalogue.regions
        val npcs = GrimNpcCatalogue.all
        
        for (entry in regions) {
            val nonlinearTime = entry.time
            val architecture = entry.architecture
            
            val consciousness = RegionConsciousness(
                regionName = entry.regionName,
                mistMind = entry.phenomenon,
                bloodBody = entry.phenomenon,
                reflectionSoul = entry.phenomenon,
                emotionalState = "neutral",
                memory = listOf("initial_seed"),
                reactions = emptyList(),
                regionImpact = "none",
                endingImpact = "none"
            )
            
            GrimGameRepository.state.grimEngine.loadRegion(consciousness, nonlinearTime, architecture)
        }
        
        for (npcName in npcs) {
            val path = GrimBuilders.basicNPCLifePath(npcName)
            GrimGameRepository.state.grimEngine.registerNPC(path, emptyList())
        }
    }
}
