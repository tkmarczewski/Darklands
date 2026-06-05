package com.grimreich.systems

import com.grimreich.core.NPC
import com.grimreich.core.WorldAI

/**
 * MutacjeNPC2.0 - System mutacji NPC dla Grimreich.
 * Zarzadza stanem NPC, reakcjami na Grimreich i zmianami zachowan.
 */
data class NPCMutation(
    val npcId: Int,
    val name: String,
    val npcType: NPCType,
    val originalState: NPCState,
    var currentState: NPCState,
    val regionId: Int,
    var grimReichAwareness: Float = 0.0f,
    val isInfested: Boolean = false,
    val mutationIntensity: Float = 0.0f
)

enum class NPCType {
    MERCHANT,
    QUEST_GIVER,
    COMPANION,
    VILLAGER,
    GUARD,
    ENEMY_NPC,
    BOSS_HUMANOID
}

enum class NPCState {
    PEACEFUL,       // Normalny stan
    ALERT,          // Podwyzszona czujnosc
    HOSTILE,        // Wrogi
    INFESTED,       // Zainfekowany Grimreich
    CORRUPTED,      // Zepsuty - wrogi boss
    FLEEING,        // Ucieka
    DEAD            // Martwy
}

object MutacjeNPC2_0 {

    private val npcMutations = mutableMapOf<Int, NPCMutation>()
    private var globalGrimreichThreat = 0.0f

    fun initialize() {
        npcMutations.clear()
        globalGrimreichThreat = 0.0f
    }

    fun addNPC(npc: NPC) {
        val originalState = determineOriginalState(npc.type)
        val mutation = NPCMutation(
            npcId = npc.id,
            name = npc.name,
            npcType = determineNPCType(npc),
            originalState = originalState,
            currentState = originalState,
            regionId = npc.regionId,
            grimReichAwareness = 0.0f
        )
        npcMutations[npc.id] = mutation
    }

    private fun determineOriginalState(npcType: String): NPCState {
        return when (npcType) {
            "hostile" -> NPCState.HOSTILE
            "neutral" -> NPCState.PEACEFUL
            "ally" -> NPCState.PEACEFUL
            "guard" -> NPCState.ALERT
            else -> NPCState.PEACEFUL
        }
    }

    private fun determineNPCType(npc: NPC): NPCType {
        return when (npc.type) {
            "merchant" -> NPCType.MERCHANT
            "quest_giver" -> NPCType.QUEST_GIVER
            "companion" -> NPCType.COMPANION
            "villager" -> NPCType.VILLAGER
            "guard" -> NPCType.GUARD
            "enemy" -> NPCType.ENEMY_NPC
            "boss" -> NPCType.BOSS_HUMANOID
            else -> NPCType.VILLAGER
        }
    }

    fun applyGrimreichExposure(npcId: Int, exposure: Float) {
        val mutation = npcMutations[npcId] ?: return
        mutation.grimReichAwareness = (mutation.grimReichAwareness + exposure).coerceIn(0.0f, 1.0f)
        updateNPCState(npcId)
    }

    fun applyGlobalThreat(threat: Float) {
        globalGrimreichThreat = threat.coerceIn(0.0f, 1.0f)
        npcMutations.forEach { (id, m) ->
            m.grimReichAwareness = (m.grimReichAwareness + threat * 0.2f).coerceIn(0.0f, 1.0f)
            updateNPCState(id)
        }
    }

    private fun updateNPCState(npcId: Int) {
        val mutation = npcMutations[npcId] ?: return
        val awareness = mutation.grimReichAwareness
        val originalState = mutation.originalState
        val npcType = mutation.npcType

        mutation.currentState = when {
            originalState == NPCState.HOSTILE && awareness >= 0.5f -> NPCState.CORRUPTED
            awareness >= 0.8f -> NPCState.INFESTED
            awareness >= 0.6f && npcType == NPCType.GUARD -> NPCState.CORRUPTED
            awareness >= 0.6f -> NPCState.HOSTILE
            awareness >= 0.4f -> NPCState.ALERT
            globalGrimreichThreat >= 0.8f && npcType == NPCType.MERCHANT -> NPCState.FLEEING
            npcType == NPCType.VILLAGER && awareness >= 0.3f -> NPCState.ALERT
            else -> originalState
        }
    }

    fun getNPCThreatLevel(npcId: Int): Float {
        val mutation = npcMutations[npcId] ?: return 0.0f
        val awarenessFactor = mutation.grimReichAwareness
        val stateFactor = when (mutation.currentState) {
            NPCState.INFESTED, NPCState.CORRUPTED -> 1.0f
            NPCState.HOSTILE -> 0.7f
            NPCState.ALERT -> 0.3f
            NPCState.FLEEING -> 0.1f
            else -> 0.0f
        }
        return (awarenessFactor * 0.7f + stateFactor * 0.3f)
    }

    fun getModifiedMerchantPrices(npcId: Int): Float {
        val mutation = npcMutations[npcId] ?: return 1.0f
        return 1.0f + mutation.grimReichAwareness * 0.5f
    }

    fun canNPCGiveQuest(npcId: Int): Boolean {
        val currentState = npcMutations[npcId]?.currentState ?: return false
        return currentState == NPCState.PEACEFUL || currentState == NPCState.ALERT
    }

    fun getوادnpcBehavior(npcId: Int): NPCBehavior {
        val mutation = npcMutations[npcId] ?: return NPCBehavior.NEUTRAL
        return when (mutation.currentState) {
            NPCState.INFESTED, NPCState.CORRUPTED -> NPCBehavior.ATTACK
            NPCState.HOSTILE -> NPCBehavior.AGGRESSIVE
            NPCState.ALERT -> NPCBehavior.WARY
            NPCState.FLEEING -> NPCBehavior.FLEE
            NPCState.DEAD -> NPCBehavior.NONE
            NPCState.PEACEFUL -> NPCBehavior.NEUTRAL
        }
    }

    enum class NPCBehavior {
        NONE,
        NEUTRAL,
        WARY,
        FLEE,
        AGGRESSIVE,
        ATTACK
    }

    fun killNPC(npcId: Int) {
        npcMutations[npcId]?.let { m ->
            m.currentState = NPCState.DEAD
        }
    }

    fun isNPCCorrupted(npcId: Int): Boolean {
        val currentState = npcMutations[npcId]?.currentState ?: return false
        return currentState == NPCState.INFESTED || currentState == NPCState.CORRUPTED
    }

    fun getNPC(npcId: Int): NPCMutation? = npcMutations[npcId]
    fun getAllNPCs(): Collection<NPCMutation> = npcMutations.values
    fun getCorruptedNPCs(): List<NPCMutation> = npcMutations.values.filter { it.isInfested || it.currentState == NPCState.CORRUPTED }
    fun getNPCTerraformedNPCs(): List<NPCMutation> = npcMutations.values.filter { it.currentState == NPCState.DEAD }
}
