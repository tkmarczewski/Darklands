package com.darklandsmobile.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class GrimWorldModuleTest {
    @Test fun definitionMatchesManifest() {
        val def = GrimWorldModule.definition
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.MIST))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.BLOOD))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.REFLECTION))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.FULLNESS))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.RIFT))
        assertTrue(def.corePhenomena.contains(GrimPhenomenon.ABSOLUTE))
        assertEquals("NonlinearTime", def.timeModel)
        assertTrue(def.hasTriLayerNpcRelations)
        assertTrue(def.hasOtherSide)
    }
}
