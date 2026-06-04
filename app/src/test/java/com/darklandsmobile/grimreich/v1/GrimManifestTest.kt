package com.darklandsmobile.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class GrimManifestTest {
  @Test fun definition_matches_manifest(){ assertTrue(GrimWorldModule.definition.corePhenomena.contains(GrimPhenomenon.MIST)); assertTrue(GrimWorldModule.definition.corePhenomena.contains(GrimPhenomenon.BLOOD)); assertTrue(GrimWorldModule.definition.corePhenomena.contains(GrimPhenomenon.REFLECTION)); assertTrue(GrimWorldModule.definition.corePhenomena.contains(GrimPhenomenon.FULLNESS)); assertTrue(GrimWorldModule.definition.corePhenomena.contains(GrimPhenomenon.RIFT)); assertTrue(GrimWorldModule.definition.corePhenomena.contains(GrimPhenomenon.ABSOLUTE)); assertEquals("NonlinearTime", GrimWorldModule.definition.timeModel); assertTrue(GrimWorldModule.definition.hasTriLayerNpcRelations); assertTrue(GrimWorldModule.definition.hasOtherSide) }
}
