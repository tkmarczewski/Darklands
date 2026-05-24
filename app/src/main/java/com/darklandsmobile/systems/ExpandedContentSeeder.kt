package com.darklandsmobile.systems

import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.world.CityCatalogue

/**
 * Adds extra event and quest seeds for a richer prototype slice.
 */
object ExpandedContentSeeder {
    fun seed(seed: Int = 21) {
        CityCatalogue.seedSprint1()
        WorldMap.seedStage1()
        CityEventSystem.seedStage1Events()
        QuestSystem.seedIntegratedContent(seed)

        CityEventSystem.register(
            CityEvent(cityId = "magdeburg", id = "evt_magdeburg_guild_audit", title = "Guild Audit", description = "Merchants quietly seek discreet help before the books are inspected.", minReputation = 0)
        )
        CityEventSystem.register(
            CityEvent(cityId = "praha", id = "evt_praha_relic_procession", title = "Relic Procession", description = "A holy procession needs eyes watching the crowd.", minReputation = 5)
        )
        CityEventSystem.register(
            CityEvent(cityId = "wroclaw", id = "evt_wroclaw_bridge_toll", title = "Bridge Toll Dispute", description = "Boatmen and guards are close to open violence over a disputed toll.", minReputation = 0)
        )

        QuestSystem.register(
            QuestEntry(id = "quest_magdeburg_delivery_letters", title = "Carry Sealed Letters", cityId = "magdeburg", rewardGold = 60, status = QuestStatus.AVAILABLE)
        )
        QuestSystem.register(
            QuestEntry(id = "quest_praha_relic_watch", title = "Watch the Relic Train", cityId = "praha", rewardGold = 120, status = QuestStatus.AVAILABLE)
        )
        QuestSystem.register(
            QuestEntry(id = "quest_wroclaw_bandit_bridges", title = "Bandits Under the Bridges", cityId = "wroclaw", rewardGold = 110, status = QuestStatus.AVAILABLE)
        )
        QuestSystem.register(
            QuestEntry(id = "quest_vienna_court_delivery", title = "Deliver to the Court", cityId = "vienna", rewardGold = 95, status = QuestStatus.AVAILABLE)
        )
    }
}
