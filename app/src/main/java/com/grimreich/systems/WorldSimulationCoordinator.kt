package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorldSimulationCoordinator @Inject constructor(
    private val gameRepository: GameRepository,
    private val worldSimulation2_0: WorldSimulation2_0,
    private val aiDirector: WorldAIDirector,
    private val npcAi: NpcAI,
    private val regionAi: RegionAI
) {
    /**
     * Executes a global world tick.
     * PERFORMANCE FIX: Grouped all AI and simulation logic into a single updateState block
     * to avoid multiple deepCopies of the game state per tick.
     */
    fun executeTick() {
        gameRepository.updateState { state ->
            // 1. Core simulation (day progress etc handled via worldSimulation)
            // Note: worldSimulation2_0 currently has no 'direct' method, but its sim logic 
            // is small. Let's assume we want to keep it modular.
            
            // Actually, let's keep executeTick as the orchestrator.
            // We need to pass 'state' to sub-systems.
            
            val currentCity = state.grimCurrentRegion
            
            // 2. Region AI
            regionAi.tickRegionDirect(state, currentCity)
            
            // 3. NPC/Hero AI
            state.party.forEach { hero ->
                npcAi.tickNpcDirect(state, hero.id)
            }
            
            // 4. World AI Director
            aiDirector.onTickDirect(state)
            
            // 5. Global Simulation
            worldSimulation2_0.simulateDirect(state)
        }
        gameRepository.persistCurrentState()
    }
}
