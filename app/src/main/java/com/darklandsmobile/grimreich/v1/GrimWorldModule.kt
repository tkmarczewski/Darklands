package com.darklandsmobile.grimreich.v1

enum class GrimPhenomenon { MIST, BLOOD, REFLECTION, FULLNESS, RIFT, ABSOLUTE }

data class GrimWorldDefinition(val corePhenomena: Set<GrimPhenomenon>, val timeModel: String, val hasTriLayerNpcRelations: Boolean, val hasOtherSide: Boolean)

object GrimWorldModule {
    val definition = GrimWorldDefinition(setOf(GrimPhenomenon.MIST, GrimPhenomenon.BLOOD, GrimPhenomenon.REFLECTION, GrimPhenomenon.FULLNESS, GrimPhenomenon.RIFT, GrimPhenomenon.ABSOLUTE), "NonlinearTime", true, true)
    fun describe(): String = "GRIMREICH 1.0 — fenomeny: ${definition.corePhenomena}, czas: ${definition.timeModel}, tri-layer NPC: ${definition.hasTriLayerNpcRelations}, Druga Strona: ${definition.hasOtherSide}"
}
