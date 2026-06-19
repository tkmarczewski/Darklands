package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialEventSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun cityAudience(cityId: String, npcId: String?): String {
        return "Mieszkańcy $cityId obserwują Cię w milczeniu."
    }
}
