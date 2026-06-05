package com.grimreich.grimreich.v1

import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class OtherSideLootTableRngTest {
    @Test
    fun bonus_increases_probability_of_rare_mist() {
        val base = listOf("mist_shard")
        val noBonus = OtherSideReward(base, base, "")
        val withBonus = OtherSideReward(base, listOf("mist_shard", "bonus_mist_shard"), "")
        val table1 = OtherSideLootTable(Random(42))
        val table2 = OtherSideLootTable(Random(42))
        var rareWithout = 0
        var rareWith = 0
        repeat(100) { if (table1.generate(base, noBonus, rolls = 1).entries.any { it.id == "mist_shard_rare" }) rareWithout++ }
        repeat(100) { if (table2.generate(base, withBonus, rolls = 1).entries.any { it.id == "mist_shard_rare" }) rareWith++ }
        assertTrue(rareWith >= rareWithout)
    }
}
