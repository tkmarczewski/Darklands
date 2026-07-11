package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.systems.CollapseEngine
import com.grimreich.systems.CollapseEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorldSimulation2_0 @Inject constructor(
    private val gameRepository: GameRepository,
    private val collapseEngine: CollapseEngine
) {
    fun simulate() {
        gameRepository.updateState { s -> 
            s.world.day += 1 
            s.logEntries.add("Dzień ${s.world.day}: Symulacja świata postępuje.")
            collapseEngine.processCollapseEventDirect(s, CollapseEvent.DayEnded)
        }
    }
}
