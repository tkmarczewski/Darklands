package com.grimreich.systems

import com.grimreich.grimreich.v1.NPC
import javax.inject.Inject
import javax.inject.Singleton

enum class NPCType { MERCHANT, QUEST_GIVER, COMPANION, VILLAGER, GUARD, ENEMY_NPC, BOSS_HUMANOID }
enum class NPCState { PEACEFUL, ALERT, HOSTILE, INFESTED, CORRUPTED, FLEEING, DEAD }

data class NPCMutation(
    val npcId: Int,
    val name: String,
    val npcType: NPCType,
    val originalState: NPCState,
    var currentState: NPCState,
    val regionId: Int,
    var grimReichAwareness: Float = 0f,
    var isInfested: Boolean = false,
    var mutationIntensity: Float = 0f
)

@Singleton
class MutacjeNPC2_0 @Inject constructor() {
    private val npcMutations = mutableMapOf<Int, NPCMutation>()
    var globalGrimreichThreat: Float = 0f

    fun initialize() {
        npcMutations.clear()
    }

    fun addNPC(npc: NPC) {
        // Mock ID for legacy compatibility
        val id = npc.name.hashCode()
        if (!npcMutations.containsKey(id)) {
            npcMutations[id] = NPCMutation(
                npcId = id,
                name = npc.name,
                npcType = NPCType.VILLAGER,
                originalState = NPCState.PEACEFUL,
                currentState = NPCState.PEACEFUL,
                regionId = 0
            )
        }
    }

    fun applyGlobalThreat(threat: Float) {
        globalGrimreichThreat = threat
        npcMutations.values.forEach {
            it.grimReichAwareness += threat * 0.1f
            if (it.grimReichAwareness > 0.8f) it.isInfested = true
        }
    }

    fun getNPC(id: Int): NPCMutation? = npcMutations[id]
    fun getAllNPCs(): Collection<NPCMutation> = npcMutations.values
}
