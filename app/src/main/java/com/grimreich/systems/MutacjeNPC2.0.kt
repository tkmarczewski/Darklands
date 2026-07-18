package com.grimreich.systems

import com.grimreich.grimreich.v1.NPC
import com.grimreich.core.GameState
import kotlin.random.Random

enum class NpcType { merchant, quest_giver, companion, villager, guard, enemy_npc, boss_humanoid }
enum class NpcState { peaceful, alert, hostile, infested, corrupted, fleeing, dead }

object NpcMutationSystem {
    fun applyRandomMutation(npc: NPC, state: GameState) {
        val stability = state.world.globalStability
        if (stability < 50 && Random.nextFloat() < (1.0f - stability / 100f)) {
            // Logika mutacji wizualnej lub statystyk NPC
            android.util.Log.d("TRIBUNAL", "NPC ${npc.name} ulega mutacji echa.")
        }
    }
}
