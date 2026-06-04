package com.darklandsmobile.core

import com.darklandsmobile.grimreich.v1.*

object GrimSeed {
    fun initialize() {
        val regions = GrimRegionCatalogue.all
        val npcs = GrimNpcCatalogue.all
        
        for (region in regions) {
            val nonlinearTime = NonlinearTime(
                regionName = region.regionName,
                mistTimeLevel = 0,
                bloodTimeLevel = 0,
                reflectionTimeLevel = 0,
                fullnessTimeLevel = 0,
                chaosTimeLevel = 0,
                activeTimeEffects = emptyList(),
                regionImpact = "none",
                npcImpact = "none",
                monsterImpact = "none",
                endingImpact = "none"
            )
            
            val architecture = FullnessArchitecture(
                structureName = "${region.regionName} Heart",
                mistForm = "veiled",
                bloodForm = "flowing",
                reflectionForm = "mirrored",
                fullnessEffect = "stable",
                chaosEffect = "calm",
                regionImpact = "foundation",
                endingImpact = "none"
            )
            
            GrimGameRepository.state.grimEngine.loadRegion(region, nonlinearTime, architecture)
        }
        
        for (npc in npcs) {
            GrimGameRepository.state.grimEngine.registerNPC(npc, emptyList())
        }
    }
}
