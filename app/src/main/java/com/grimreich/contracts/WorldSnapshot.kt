package com.grimreich.contracts

/**
 * A full canonical read model of the GrimReich world at a specific point in time.
 * This is used as input for all 2.0 simulation engines.
 */
data class WorldSnapshot(
    val timestamp: Long,
    val worldSeed: Int,
    val regionState: RegionStateContract,
    val npcStates: List<NpcStateContract>,
    val collapseState: ScenarioStateContract,
    val historyState: HistoryStateContract,
    val mutationState: MutationStateContract,
    val phenomenaState: PhenomenaStateContract,
    val absoluteState: AbsoluteLayerContract
)
