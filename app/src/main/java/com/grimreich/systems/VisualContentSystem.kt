package com.grimreich.systems

import com.grimreich.core.CityHubViewData
import com.grimreich.core.CityVisual
import com.grimreich.core.PlayerState
import com.grimreich.core.QuestCardViewData

object VisualContentSystem {
    private val visuals = mapOf(
        "wybrzeze_polnocne" to CityVisual("wybrzeze_polnocne", "Wybrzeże Północne", "city_mist_cliffs", "emblem_ship", "Cold cliffs, wrecks and eternal mist."),
        "serce_krainy" to CityVisual("serce_krainy", "Serce Krainy", "city_mirror_cathedral", "emblem_mirror", "Cathedral city, mirrors and archives of truth."),
        "rowniny_koronne" to CityVisual("rowniny_koronne", "Równiny Koronne", "city_blood_canal", "emblem_blood", "Fertile fields, red canals and guild law."),
        "pogranicze_stepowe" to CityVisual("pogranicze_stepowe", "Pogranicze Stepowe", "city_rift_fort", "emblem_rift", "Steppe, rifts and shadow raids."),
        "poludniowe_ruiny" to CityVisual("poludniowe_ruiny", "Południowe Ruiny", "city_ash_temple", "emblem_hymn", "Ruined temples, ash and echoes of hymns."),
        "gory_poludniowe" to CityVisual("gory_poludniowe", "Góry Południowe", "city_ice_summit", "emblem_peak", "Ice passes, absolute silence and summits."),
        "ziemie_dzikie" to CityVisual("ziemie_dzikie", "Ziemie Dzikie", "city_wild_forest", "emblem_runes", "Forests, hunger and ancient runes.")
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
