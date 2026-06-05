package com.grimreich.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class OtherSideNpcLogicTest {
    private val logic = OtherSideNpcLogic()

    @Test
    fun loyal_npc_has_low_corruption_and_high_sanity() {
        val npc = NPCLifePath("Test NPC", "", "", "", 15, 2, emptyList(), "", "")
        val rels = listOf(TriLayerRelationship("Test NPC", "", "", "", 10, 1, "", "", ""))
        val religion = PhenomenonReligion("Neutral", "", "", emptyList(), emptyList(), emptyList(), "", "", "")
        val state = logic.evaluate(npc, rels, religion)
        assertEquals(OtherSideLoyalty.LOYAL, state.loyalty)
        assertTrue(state.sanity >= 10)
    }

    @Test
    fun betrayer_npc_has_high_corruption() {
        val npc = NPCLifePath("Cultist", "", "", "", 3, 15, emptyList(), "", "")
        val rels = listOf(TriLayerRelationship("Cultist", "", "", "", 1, 10, "", "", ""))
        val religion = PhenomenonReligion("Kult", "", "Kazdy grzech wzmacnia nicosc", emptyList(), emptyList(), emptyList(), "", "", "")
        val state = logic.evaluate(npc, rels, religion)
        assertEquals(OtherSideLoyalty.BETRAYER, state.loyalty)
        assertTrue(state.corruption >= 10)
    }
}
