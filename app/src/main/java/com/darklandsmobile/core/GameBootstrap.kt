package com.darklandsmobile.core

import com.darklandsmobile.grimreich.v1.*

object GameBootstrap {
    fun init(state: GameState = GameState()): GameState {
        seedGrimWorld(state.grimEngine)
        return state
    }
    fun initialize(): GameState = init(GameState())
    private fun seedGrimWorld(engine: GrimWorldEngine) {
        val regions = GrimRegionCatalogue.regions
        for (entry in regions) {
            engine.loadRegion(
                RegionConsciousness(entry.regionName, entry.phenomenon, entry.phenomenon, entry.phenomenon, "neutral", listOf("mem"), listOf("react"), "impact", "ending"),
                entry.time,
                entry.architecture
            )
        }
        engine.registerNPC(GrimBuilders.basicNPCLifePath("Aelion"), emptyList())
        engine.registerNPC(GrimBuilders.basicNPCLifePath("Xyrel"), emptyList())
        engine.registerNPC(GrimBuilders.basicNPCLifePath("Sereth"), emptyList())
        engine.registerReligion(PhenomenonReligion("Kult Pustego Tronu", "nicość", "Każdy grzech wzmacnia nicość", listOf("czarna_msza"), listOf("Arcycień"), listOf("Korona_Pustki"), "erozja_miast", "Inkwizytor Hagen, Alchemik Wulfram", "upadek"))
        engine.registerArtifact(FullnessArtifact("Lustro Drugiej Strony", "mgła", "krew", "odbicie", "pełnia", "rozdarcie", "region", "npc", "ending"))
        engine.setAvatar(FullnessAvatar("Cień Rycerza", "mgła", "krew", "odbicie", 1, 1, emptyList(), "region", "npc", "ending"))
        engine.startExpedition(OtherSideExpedition("Druga Strona: Wybrzeże Północne", "Logic", "Sym", "Zero", listOf("shadow_wraith"), listOf("mist_shard"), "impact", "ending", 1))
        engine.recordHistory(AlternateHistory("Historia Alternatywna", "mgła", "krew", "odbicie", "pełnia", "rozdarcie", "region", "npc", "ending"), GrimBuilders.emptyWorldChronicle("Kronika Świata"))
        engine.applyAbsoluteMutation(AbsoluteMutation("Mutant", 1, 1, 1, 1, emptyList(), "region", "npc", "monster", "ending"))
        engine.updateWorldCollapse(WorldCollapse("stage", 1, 1, 1, 1, 1, 1, "ending"))
    }
}
