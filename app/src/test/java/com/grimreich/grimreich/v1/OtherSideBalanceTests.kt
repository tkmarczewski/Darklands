package com.grimreich.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class OtherSideBalanceTests {

    @Test
    fun deathRisk_grows_with_npc_chaos() {
        val npc = NPCLifePath(
            npcName = "Test",
            mistFate = "",
            bloodFate = "",
            reflectionFate = "",
            fullnessLevel = 10,
            chaosLevel = 5,
            timelineEvents = emptyList(),
            npcImpact = "",
            endingImpact = ""
        )
        val rels = emptyList<TriLayerRelationship>()
        val religion = null
        val logic = OtherSideNpcLogic()

        val lowChaosState = logic.evaluate(npc, rels, religion)
        val highChaosState = logic.evaluate(npc.copy(chaosLevel = 10), rels, religion)

        assertTrue(highChaosState.deathRisk >= lowChaosState.deathRisk)
    }
}
