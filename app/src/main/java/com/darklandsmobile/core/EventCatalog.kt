package com.darklandsmobile.core

/**
 * Pełny katalog eventów miejskich (CITY context) na podstawie oryginalnego Darklands.
 * Obejmuje: karczmę, straż, zasiączki, kościół, gildie, handlarzy, plotki.
 */

object CityEventCatalog {

    private val EV_INN_ENTRY = EventId("ev_city_inn_entry")
    private val EV_INN_BRAWL = EventId("ev_city_inn_brawl")
    private val EV_CITY_MUGGING = EventId("ev_city_mugging")
    private val EV_CITY_WATCH_PATROL = EventId("ev_city_watch_patrol")
    private val EV_CHURCH_ENTRY = EventId("ev_church_entry")
    private val EV_GUILD_ALCHEMY = EventId("ev_guild_alchemy")
    private val EV_MARKETPLACE = EventId("ev_marketplace")
    private val EV_RUMOR_RAUBRITTER = EventId("ev_rumor_raubritter")

    fun buildEvents(): List<Event> = listOf(
        // ────── KARCZMA ──────────────────────────────────────────
        Event(
            id = EV_INN_ENTRY,
            context = EventContext.CITY,
            category = EventCategory.CITY_SERVICE,
            weight = 5,
            rootNodeId = EventNodeId("inn_entry_n1"),
            tags = setOf("inn", "rest")
        ),
        Event(
            id = EV_INN_BRAWL,
            context = EventContext.CITY,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 2,
            rootNodeId = EventNodeId("inn_brawl_n1"),
            conditions = listOf(
                TimeOfDayCondition(setOf(TimeOfDay.Evening, TimeOfDay.Night))
            ),
            tags = setOf("inn", "combat")
        ),
        // ────── ULICE / ZASIĄCZKI ────────────────────────────────
        Event(
            id = EV_CITY_MUGGING,
            context = EventContext.CITY,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 3,
            rootNodeId = EventNodeId("mugging_n1"),
            conditions = listOf(
                TimeOfDayCondition(setOf(TimeOfDay.Night))
            ),
            tags = setOf("alley", "crime")
        ),
        // ────── STRAŻ ────────────────────────────────────────────
        Event(
            id = EV_CITY_WATCH_PATROL,
            context = EventContext.CITY,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 4,
            rootNodeId = EventNodeId("watch_patrol_n1"),
            tags = setOf("guard", "law")
        ),
        // ────── KOŚCIÓŁ ──────────────────────────────────────────
        Event(
            id = EV_CHURCH_ENTRY,
            context = EventContext.CITY,
            category = EventCategory.CITY_SERVICE,
            weight = 5,
            rootNodeId = EventNodeId("church_entry_n1"),
            tags = setOf("church", "religion")
        ),
        // ────── GILDIA ALCHEMIKÓW ────────────────────────────────
        Event(
            id = EV_GUILD_ALCHEMY,
            context = EventContext.CITY,
            category = EventCategory.CITY_SERVICE,
            weight = 3,
            rootNodeId = EventNodeId("guild_alchemy_n1"),
            conditions = listOf(
                LocationTagCondition(setOf("guild_quarter"))
            ),
            tags = setOf("guild", "alchemy")
        ),
        // ────── TARG / HANDLARZ ──────────────────────────────────
        Event(
            id = EV_MARKETPLACE,
            context = EventContext.CITY,
            category = EventCategory.CITY_SERVICE,
            weight = 6,
            rootNodeId = EventNodeId("marketplace_n1"),
            tags = setOf("market", "trade")
        ),
        // ────── PLOTKI ───────────────────────────────────────────
        Event(
            id = EV_RUMOR_RAUBRITTER,
            context = EventContext.CITY,
            category = EventCategory.QUEST,
            weight = 2,
            rootNodeId = EventNodeId("rumor_raubritter_n1"),
            tags = setOf("rumor", "quest")
        )
    )

    fun buildNodes(): List<EventNode> = listOf(
        // ═══════ KARCZMA: wejście ═════════════════════════════════
        EventNode(
            id = EventNodeId("inn_entry_n1"),
            eventId = EV_INN_ENTRY,
            textKey = "inn.entry.desc",
            illustrationAsset = "inn_interior",
            options = listOf(
                EventOption(
                    id = EventOptionId("inn_rest"),
                    textKey = "inn.rest",
                    outcome = ChainOutcome(listOf(
                        ModifyResourcesOutcome(goldDelta = -10),
                        ModifyTimeOutcome(hours = 8),
                        ModifyHealthOutcome(hpDeltaPerHero = 20),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("inn_drink"),
                    textKey = "inn.drink",
                    outcome = ChainOutcome(listOf(
                        ModifyResourcesOutcome(goldDelta = -5),
                        ModifyVirtueOutcome(mapOf(Virtue.Piety to -1)),
                        EventEnd(EventEndResult.Neutral)
                    ))
                ),
                EventOption(
                    id = EventOptionId("inn_leave"),
                    textKey = "inn.leave",
                    outcome = EventEnd(EventEndResult.Neutral)
                )
            )
        ),
        // ═══════ KARCZMA: awantura ════════════════════════════════
        EventNode(
            id = EventNodeId("inn_brawl_n1"),
            eventId = EV_INN_BRAWL,
            textKey = "inn.brawl.desc",
            illustrationAsset = "inn_brawl",
            options = listOf(
                EventOption(
                    id = EventOptionId("brawl_fight"),
                    textKey = "inn.brawl.fight",
                    outcome = StartCombatOutcome(
                        encounterId = "combat_inn_brawl",
                        surpriseParty = false
                    )
                ),
                EventOption(
                    id = EventOptionId("brawl_defuse"),
                    textKey = "inn.brawl.defuse",
                    requirements = listOf(
                        SkillRequirement(HeroSkill.Speak, 40)
                    ),
                    outcome = ChainOutcome(listOf(
                        ModifyReputationOutcome(mapOf(Faction.Locals to 5)),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("brawl_flee"),
                    textKey = "inn.brawl.flee",
                    outcome = ChainOutcome(listOf(
                        ModifyReputationOutcome(mapOf(Faction.Locals to -10)),
                        EventEnd(EventEndResult.Escape)
                    ))
                )
            )
        ),
        // ═══════ ZASIĄCZKA: napad ═════════════════════════════════
        EventNode(
            id = EventNodeId("mugging_n1"),
            eventId = EV_CITY_MUGGING,
            textKey = "city.mugging.desc",
            illustrationAsset = "alley_night",
            options = listOf(
                EventOption(
                    id = EventOptionId("mugging_fight"),
                    textKey = "city.mugging.fight",
                    outcome = StartCombatOutcome(
                        encounterId = "combat_alley_thieves",
                        surpriseParty = true
                    )
                ),
                EventOption(
                    id = EventOptionId("mugging_stealth"),
                    textKey = "city.mugging.stealth",
                    requirements = listOf(
                        SkillRequirement(HeroSkill.Stealth, 50)
                    ),
                    outcome = ChainOutcome(listOf(
                        ModifyTimeOutcome(hours = 1),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("mugging_pay"),
                    textKey = "city.mugging.pay",
                    outcome = ChainOutcome(listOf(
                        ModifyResourcesOutcome(goldDelta = -50),
                        ModifyReputationOutcome(mapOf(Faction.Thieves to 5)),
                        EventEnd(EventEndResult.Failure)
                    ))
                )
            )
        )
    )
}
