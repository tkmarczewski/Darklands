package com.grimreich.systems

import com.grimreich.core.CityHubViewData
import com.grimreich.core.CityVisual
import com.grimreich.core.PlayerState
import com.grimreich.core.QuestCardViewData

object VisualContentSystem {
    private val visuals = mapOf(
        "grimhold" to CityVisual("grimhold", "Grimhold", "city_grimhold_docks", "emblem_eagle", "Busy river trade, suspicious alleys, restless guild politics."),
        "praha" to CityVisual("praha", "Praha", "city_praha_castle", "emblem_lion", "Scholars, nobles and intrigue under cathedral shadows."),
        "koln" to CityVisual("koln", "Koln", "city_koln_cathedral", "emblem_spire", "Pilgrims, relics and merchant traffic around the great cathedral."),
        "brno" to CityVisual("brno", "Brno", "city_brno_market", "emblem_gate", "Frontier bargaining, caravan gossip and guarded optimism."),
        "wroclaw" to CityVisual("wroclaw", "Wroclaw", "city_wroclaw_bridges", "emblem_raven", "Islands, bridges and deals whispered at dusk."),
        "vienna" to CityVisual("vienna", "Vienna", "city_vienna_walls", "emblem_crown", "Power, ceremony and expensive promises behind strong walls.")
    )

    fun cityHub(playerState: PlayerState): CityHubViewData {
        val visual = visuals[playerState.currentCityId]
            ?: CityVisual(playerState.currentCityId, playerState.currentCityId.replaceFirstChar { it.uppercase() }, "city_generic", "emblem_generic", "A hard road town with stories in every tavern.")

        val cards = QuestSystem.availableForCity(playerState.currentCityId).map { quest ->
            QuestCardViewData(
                questId = quest.id,
                title = quest.title,
                cityId = quest.cityId,
                rewardGold = quest.rewardGold,
                status = when {
                    quest.id == playerState.activeQuestId -> "ACTIVE"
                    quest.id in playerState.completedQuestIds -> "COMPLETED"
                    else -> quest.status.name
                },
                accentLabel = if (quest.rewardGold >= 100) "High reward" else "Local work",
                flavorText = flavorForQuest(quest.id, quest.cityId)
            )
        }

        return CityHubViewData(
            cityId = playerState.currentCityId,
            cityTitle = visual.title,
            gold = playerState.gold,
            activeQuestId = playerState.activeQuestId,
            moodText = visual.moodText,
            backdropName = visual.backdropName,
            emblemName = visual.emblemName,
            questCards = cards
        )
    }

    private fun flavorForQuest(questId: String, cityId: String): String = when {
        questId.contains("escort") -> "Someone important wants guards on a dangerous road out of $cityId."
        questId.contains("relic") -> "Rumours of sacred value draw both pilgrims and thieves."
        questId.contains("bandit") -> "Local roads are no longer safe after dark."
        questId.contains("delivery") -> "A fast, discreet handoff could shift local politics."
        else -> "The work sounds simple, which usually means it is not."
    }
}
