package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialEventSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val cityCatalogue: CityCatalogue
) {
    fun cityAudience(cityId: String, npcId: String?): String {
        val cityName = cityCatalogue.get(cityId)?.name ?: cityId
        return "Mieszkańcy $cityName obserwują Cię w milczeniu."
    }
}
