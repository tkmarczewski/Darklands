package com.grimreich.systems

import com.grimreich.core.WorldMap
import com.grimreich.world.CityCatalogue

object ExpandedContentSeeder {
    fun seed(seed: Int = 21) {
        CityCatalogue.seedSprint1()
        WorldMap.seedStage1()
        CityEventSystem.seedStage1Events()
        QuestSystem.seedIntegratedContent(seed)

        CityEventSystem.register(
            CityEvent(
                cityId = "grimhold",
                id = "evt_grimhold_guild_audit",
                title = "Guild Audit",
                description = "Merchants quietly seek discreet help before the books are inspected."
            )
        )
        CityEventSystem.register(
            CityEvent(
                cityId = "praha",
                id = "evt_praha_relic_procession",
                title = "Relic Procession",
                description = "A holy procession needs eyes watching the crowd."
            )
        )
        CityEventSystem.register(
            CityEvent(
                cityId = "wroclaw",
                id = "evt_wroclaw_bridge_toll",
                title = "Bridge Toll Dispute",
                description = "Boatmen and guards are close to open violence over a disputed toll."
            )
        )

        QuestSystem.register(
            QuestEntry(
                id = "quest_grimhold_delivery_letters",
                title = "Carry Sealed Letters",
                description = "A sealed bundle of letters must reach trusted hands in Grimhold.",
                cityId = "grimhold",
                originType = QuestOriginType.CITY_EVENT,
                originRefId = "evt_grimhold_guild_audit",
                rewardGold = 60,
                status = QuestStatus.AVAILABLE
            )
        )
        QuestSystem.register(
            QuestEntry(
                id = "quest_praha_relic_watch",
                title = "Watch the Relic Train",
                description = "Guard the route of a sacred procession through restless streets.",
                cityId = "praha",
                originType = QuestOriginType.CITY_EVENT,
                originRefId = "evt_praha_relic_procession",
                rewardGold = 120,
                status = QuestStatus.AVAILABLE
            )
        )
        QuestSystem.register(
            QuestEntry(
                id = "quest_wroclaw_bandit_bridges",
                title = "Bandits Under the Bridges",
                description = "Clear out raiders preying on travelers beneath the bridges.",
                cityId = "wroclaw",
                originType = QuestOriginType.CITY_EVENT,
                originRefId = "evt_wroclaw_bridge_toll",
                rewardGold = 110,
                status = QuestStatus.AVAILABLE
            )
        )
        QuestSystem.register(
            QuestEntry(
                id = "quest_vienna_court_delivery",
                title = "Deliver to the Court",
                description = "Carry a discreet court delivery without drawing attention.",
                cityId = "vienna",
                originType = QuestOriginType.CITY_EVENT,
                originRefId = "vienna_court_delivery",
                rewardGold = 95,
                status = QuestStatus.AVAILABLE
            )
        )
    }
}