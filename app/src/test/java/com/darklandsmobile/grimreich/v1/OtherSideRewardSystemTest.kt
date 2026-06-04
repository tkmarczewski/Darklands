package com.darklandsmobile.grimreich.v1

import org.junit.Assert.*
import org.junit.Test

class OtherSideRewardSystemTest {
    private val system = OtherSideRewardSystem()

    @Test
    fun no_npc_no_modifiers() {
        val base = listOf("mist_shard", "blood_seal")
        val reward = system.applyNpcModifiers(base, emptyList())
        assertEquals(base, reward.finalRewards)
    }

    @Test
    fun betrayer_adds_penalty_curse() {
        val npc = OtherSideNpcState("X", OtherSideLoyalty.BETRAYER, 1, 18, 20, -2, "")
        val reward = system.applyNpcModifiers(listOf("mist_shard"), listOf(npc))
        assertTrue(reward.finalRewards.contains("penalty_curse"))
    }
}
