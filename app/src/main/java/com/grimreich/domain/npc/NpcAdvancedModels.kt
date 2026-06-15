package com.grimreich.domain.npc

import com.grimreich.domain.mutation.MutationEffect

data class NpcCognition(
    val perception: Float,
    val stability: Float,
    val echoSusceptibility: Float
)

data class NpcMemoryVersion(
    val id: String,
    val timelineId: String,
    val facts: Map<String, String>
)

data class NpcAdvancedState(
    val id: String,
    val cognition: NpcCognition,
    val activeMutations: List<MutationEffect>,
    val memoryVersions: List<NpcMemoryVersion>,
    val activeMemoryVersionId: String
)
