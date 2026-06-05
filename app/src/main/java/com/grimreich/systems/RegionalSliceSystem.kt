package com.grimreich.systems

import com.grimreich.core.PlayerState
import com.grimreich.core.RegionalSliceViewData
import com.grimreich.core.SliceQuestDetail

object RegionalSliceSystem {
    private data class SliceDef(
        val title: String,
        val mood: String,
        val backgroundUrl: String,
        val referenceTitle: String,
        val quests: List<QuestEntry>
    )

    private val defs = mapOf(
        "praha" to SliceDef(
            title = "Praha",
            mood = "Bell towers, scholars and conspiracies beneath cathedral stone.",
            backgroundUrl = "https://commons.wikimedia.org/wiki/Special:FilePath/Prague%20Castle%20as%20seen%20at%20night.jpg",
            referenceTitle = "Prague Castle as seen at night",
            quests = listOf(
                QuestEntry(
                    id = "quest_praha_cathedral_watch",
                    title = "Guard the Cathedral Steps",
                    description = "Keep watch near the cathedral after dark.",
                    cityId = "praha",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "praha_cathedral_watch",
                    rewardGold = 95,
                    status = QuestStatus.AVAILABLE
                ),
                QuestEntry(
                    id = "quest_praha_alchemist_debt",
                    title = "Collect the Alchemist's Debt",
                    description = "Recover payment owed to a nervous alchemist.",
                    cityId = "praha",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "praha_alchemist_debt",
                    rewardGold = 80,
                    status = QuestStatus.AVAILABLE
                ),
                QuestEntry(
                    id = "quest_praha_scriptorium_fire",
                    title = "Investigate the Scriptorium Fire",
                    description = "Find out who profited from the fire in the scriptorium.",
                    cityId = "praha",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "praha_scriptorium_fire",
                    rewardGold = 120,
                    status = QuestStatus.AVAILABLE
                )
            )
        ),
        "koln" to SliceDef(
            title = "Koln",
            mood = "Relics, docked barges and towering stone over crowded pilgrims.",
            backgroundUrl = "https://commons.wikimedia.org/wiki/Special:FilePath/Cologne%20Cathedral%20at%20night%20-%20Cologne%2C%20Germany%20-%20DSC09696.jpg",
            referenceTitle = "Cologne Cathedral at night",
            quests = listOf(
                QuestEntry(
                    id = "quest_koln_relic_guard",
                    title = "Guard the Pilgrim Relics",
                    description = "Keep relic traffic secure during the evening rush.",
                    cityId = "koln",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "koln_relic_guard",
                    rewardGold = 100,
                    status = QuestStatus.AVAILABLE
                ),
                QuestEntry(
                    id = "quest_koln_wharf_extortion",
                    title = "Break the Wharf Extortion Ring",
                    description = "Find who is squeezing the dockworkers for coin.",
                    cityId = "koln",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "koln_wharf_extortion",
                    rewardGold = 90,
                    status = QuestStatus.AVAILABLE
                ),
                QuestEntry(
                    id = "quest_koln_cathedral_letters",
                    title = "Carry Letters to the Chapter",
                    description = "Deliver sealed letters before sunrise prayers.",
                    cityId = "koln",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "koln_cathedral_letters",
                    rewardGold = 75,
                    status = QuestStatus.AVAILABLE
                )
            )
        ),
        "brno" to SliceDef(
            title = "Brno",
            mood = "Market murmurs, guarded gates and uneasy frontier commerce.",
            backgroundUrl = "https://commons.wikimedia.org/wiki/Special:FilePath/Brno%20-%20Zeln%C3%BD%20trh%20-%20night.jpg",
            referenceTitle = "Night in Brno reference",
            quests = listOf(
                QuestEntry(
                    id = "quest_brno_market_spy",
                    title = "Find the Market Informant",
                    description = "A whisper network in the market hides one useful witness.",
                    cityId = "brno",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "brno_market_spy",
                    rewardGold = 70,
                    status = QuestStatus.AVAILABLE
                ),
                QuestEntry(
                    id = "quest_brno_gate_patrol",
                    title = "Reinforce the South Gate",
                    description = "Join the watch before tensions spill into violence.",
                    cityId = "brno",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "brno_gate_patrol",
                    rewardGold = 85,
                    status = QuestStatus.AVAILABLE
                ),
                QuestEntry(
                    id = "quest_brno_caravan_oath",
                    title = "Witness a Caravan Oath",
                    description = "Stand witness to a deal that may not hold until dawn.",
                    cityId = "brno",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "brno_caravan_oath",
                    rewardGold = 95,
                    status = QuestStatus.AVAILABLE
                )
            )
        ),
        "wroclaw" to SliceDef(
            title = "Wroclaw",
            mood = "Bridge lanterns, island churches and bargains made over dark water.",
            backgroundUrl = "https://commons.wikimedia.org/wiki/Special:FilePath/Tumski%20Bridge%20in%20Wroc%C5%82aw%2C%20Poland.jpg",
            referenceTitle = "Tumski Bridge in Wroclaw",
            quests = listOf(
                QuestEntry(
                    id = "quest_wroclaw_bridge_watch",
                    title = "Watch the Tumski Crossing",
                    description = "Observe suspicious movement across the bridge after dark.",
                    cityId = "wroclaw",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "wroclaw_bridge_watch",
                    rewardGold = 90,
                    status = QuestStatus.AVAILABLE
                ),
                QuestEntry(
                    id = "quest_wroclaw_boatmen_dispute",
                    title = "Settle the Boatmen Dispute",
                    description = "Calm a river quarrel before blades come out.",
                    cityId = "wroclaw",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "wroclaw_boatmen_dispute",
                    rewardGold = 80,
                    status = QuestStatus.AVAILABLE
                ),
                QuestEntry(
                    id = "quest_wroclaw_river_relic",
                    title = "Recover the River Relic",
                    description = "Track a sacred object lost in the river trade.",
                    cityId = "wroclaw",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "wroclaw_river_relic",
                    rewardGold = 110,
                    status = QuestStatus.AVAILABLE
                )
            )
        ),
        "vienna" to SliceDef(
            title = "Vienna",
            mood = "Court whispers, guarded treasure and ambition behind heavy walls.",
            backgroundUrl = "https://commons.wikimedia.org/wiki/Special:FilePath/St.%20Stephen%27s%20Cathedral%20Vienna%20April%202007%20front.jpg",
            referenceTitle = "Vienna cathedral reference",
            quests = listOf(
                QuestEntry(
                    id = "quest_vienna_court_mask",
                    title = "Deliver a Masked Invitation",
                    description = "Carry a discreet invitation through the court district.",
                    cityId = "vienna",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "vienna_court_mask",
                    rewardGold = 100,
                    status = QuestStatus.AVAILABLE
                ),
                QuestEntry(
                    id = "quest_vienna_treasury_rumor",
                    title = "Trace the Treasury Rumor",
                    description = "Follow a dangerous rumor before it reaches the wrong ears.",
                    cityId = "vienna",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "vienna_treasury_rumor",
                    rewardGold = 120,
                    status = QuestStatus.AVAILABLE
                ),
                QuestEntry(
                    id = "quest_vienna_watch_captain",
                    title = "Aid the Watch Captain",
                    description = "Support the city watch in a politically sensitive matter.",
                    cityId = "vienna",
                    originType = QuestOriginType.CITY_EVENT,
                    originRefId = "vienna_watch_captain",
                    rewardGold = 85,
                    status = QuestStatus.AVAILABLE
                )
            )
        )
    )

    fun seedAll() {
        ExpandedContentSeeder.seed(seed = 55)
        defs.values.flatMap { it.quests }.forEach { QuestSystem.register(it) }
    }

    fun view(cityId: String, playerState: PlayerState): RegionalSliceViewData {
        val def = defs[cityId] ?: error("No slice for city: $cityId")
        val quests = QuestSystem.availableForCity(cityId)
            .take(5)
            .map {
                SliceQuestDetail(
                    questId = it.id,
                    title = it.title,
                    shortBrief = brief(it.id, cityId),
                    rewardGold = it.rewardGold,
                    difficultyLabel = when {
                        it.rewardGold >= 110 -> "Hard"
                        it.rewardGold >= 85 -> "Medium"
                        else -> "Easy"
                    }
                )
            }

        return RegionalSliceViewData(
            cityId = cityId,
            cityTitle = def.title,
            moodText = def.mood,
            backgroundUrl = def.backgroundUrl,
            referenceTitle = def.referenceTitle,
            sourceLabel = "Reference image for internal playtests",
            gold = playerState.gold,
            activeQuestId = playerState.activeQuestId,
            quests = quests
        )
    }

    private fun brief(id: String, cityId: String): String = when {
        id.contains("relic") -> "Something sacred and valuable is drawing trouble in $cityId."
        id.contains("guard") || id.contains("watch") -> "Keep order where nerves are already stretched thin."
        id.contains("letters") || id.contains("invitation") -> "A discreet message could reshape the local balance of power."
        id.contains("dispute") || id.contains("debt") -> "A local conflict is one bad night away from bloodshed."
        else -> "Routine work with the smell of conspiracy underneath."
    }
}