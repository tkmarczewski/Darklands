package com.grimreich.systems

import com.grimreich.core.PlayerState
import javax.inject.Inject
import javax.inject.Singleton

data class CityHubViewData(
    val cityName: String,
    val description: String,
    val background: String,
    val activeQuests: List<String>
)

@Singleton
class VisualContentSystem @Inject constructor(
    private val questSystem: QuestSystem
) {
    fun cityHub(playerState: PlayerState): CityHubViewData {
        val quests = questSystem.availableForCity(playerState.currentCityId)
        
        return CityHubViewData(
            cityName = playerState.currentCityId.uppercase(),
            description = "Witaj w ${playerState.currentCityId}.",
            background = "bg_city",
            activeQuests = quests.map { it.title }
        )
    }

    fun flavorForQuest(questId: String, cityId: String): String {
        val q = questSystem.getQuest(questId)
        return q?.description ?: "Brak opisu dla $questId w $cityId."
    }
}
