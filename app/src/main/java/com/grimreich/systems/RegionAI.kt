package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegionAI @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun tickRegion(cityId: String) {
        val intensity = gameRepository.currentState().world.echoIntensity
        if (intensity > 0.5f) {
            gameRepository.log("Region $cityId wibruje echem.")
        }
    }
}
