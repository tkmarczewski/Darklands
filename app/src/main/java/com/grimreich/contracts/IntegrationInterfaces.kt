package com.grimreich.contracts

/**
 * Interface for providing a [WorldSnapshot] from the current game state.
 */
interface WorldSnapshotProvider {
    fun captureSnapshot(): WorldSnapshot
}

/**
 * Interface for resolving and applying mutations (diffs) back to the game state.
 */
interface WorldMutationResolver {
    fun resolve(snapshot: WorldSnapshot, context: SimulationTickContext)
}
