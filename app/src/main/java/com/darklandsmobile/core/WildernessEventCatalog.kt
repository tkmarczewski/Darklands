package com.darklandsmobile.core

/**
 * Katalog eventów typu WILDERNESS (podroże po mapie) na podstawie Darklands.
 * Obejmuje: bandytów, wilki, karawany, pielgrzymów, zwiadowców, znąchorkę, kult, most, klaszto, pustelnika, uchodźców, opuszczony obóz, kupca pod atakiem.
 */

object WildernessEventCatalog {

    private val EV_BANDITS = EventId("ev_wild_bandits")
    private val EV_WOLVES = EventId("ev_wild_wolves")
    private val EV_PILGRIMS = EventId("ev_wild_pilgrims")
    private val EV_CARAVAN = EventId("ev_wild_caravan")
    private val EV_SCOUTS = EventId("ev_wild_scouts")
    private val EV_WITCH = EventId("ev_wild_witch")
    private val EV_CULT_MEETING = EventId("ev_wild_cult")
    private val EV_BRIDGE_TOLL = EventId("ev_wild_bridge")
    private val EV_MONASTERY = EventId("ev_wild_monastery")
        private val EV_HERMIT = EventId("ev_wild_hermit")
            private val EV_REFUGEES = EventId("ev_wild_refugees")
                private val EV_ABANDONED_CAMP = EventId("ev_wild_abandoned_camp")
                    private val EV_MERCHANT_ATTACK = EventId("ev_wild_merchant_attack")

    fun buildEvents(): List<Event> = listOf(
        // ────── BANDYCI ─────────────────────────────────────────────
        Event(
            id = EV_BANDITS,
            context = EventContext.WILDERNESS,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 8,
            rootNodeId = EventNodeId("bandits_n1"),
            tags = setOf("combat", "crime")
        ),
        // ────── WILKI ───────────────────────────────────────────────
        Event(
            id = EV_WOLVES,
            context = EventContext.WILDERNESS,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 5,
            rootNodeId = EventNodeId("wolves_n1"),
            conditions = listOf(
                TimeOfDayCondition(setOf(TimeOfDay.Night, TimeOfDay.Evening))
            ),
            tags = setOf("combat", "animal")
        ),
        // ────── PIELGRZYMI ─────────────────────────────────────────
        Event(
            id = EV_PILGRIMS,
            context = EventContext.WILDERNESS,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 3,
            rootNodeId = EventNodeId("pilgrims_n1"),
            tags = setOf("friendly", "religion")
        ),
        // ────── KARAWANA ───────────────────────────────────────────
        Event(
            id = EV_CARAVAN,
            context = EventContext.WILDERNESS,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 4,
            rootNodeId = EventNodeId("caravan_n1"),
            tags = setOf("trade", "friendly")
        ),
        // ────── ZWIADOWCY RAUBRITTERA ───────────────────────────────
        Event(
            id = EV_SCOUTS,
            context = EventContext.WILDERNESS,
            category = EventCategory.QUEST,
            weight = 2,
            rootNodeId = EventNodeId("scouts_n1"),
            conditions = listOf(
                QuestStateCondition("quest_raubritter", "active")
            ),
            tags = setOf("quest", "combat")
        ),
        // ────── ZNĄCHORKA ──────────────────────────────────────────
        Event(
            id = EV_WITCH,
            context = EventContext.WILDERNESS,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 2,
            rootNodeId = EventNodeId("witch_n1"),
            tags = setOf("alchemy", "occult")
        ),
        // ────── KULT / OBRZĘD ─────────────────────────────────────
        Event(
            id = EV_CULT_MEETING,
            context = EventContext.WILDERNESS,
            category = EventCategory.STORY,
            weight = 1,
            rootNodeId = EventNodeId("cult_n1"),
            conditions = listOf(
                TimeOfDayCondition(setOf(TimeOfDay.Night))
            ),
            tags = setOf("cult", "combat", "endgame")
        ),
        // ────── MOST Z MYTEM ─────────────────────
        Event(
            id = EV_BRIDGE_TOLL,
            context = EventContext.WILDERNESS,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 2,
            rootNodeId = EventNodeId("bridge_n1"),
            tags = setOf("toll", "trade")
        ),
        // ────── KLASZTOR ────────────────────────────────────────────
        Event(
            id = EV_MONASTERY,
            context = EventContext.WILDERNESS,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 1,
            rootNodeId = EventNodeId("monastery_n1"),
            tags = setOf("religion", "rest")
        ),
        // ———————— PUSTELNIK ————————————————————————
        Event(
            id = EV_HERMIT,
            context = EventContext.WILDERNESS,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 2,
            rootNodeId = EventNodeId("hermit_n1"),
            tags = setOf("peaceful", "wisdom")
        ),
        // ———————— UCHODŹCY ————————————————————————
        Event(
            id = EV_REFUGEES,
            context = EventContext.WILDERNESS,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 3,
            rootNodeId = EventNodeId("refugees_n1"),
            tags = setOf("charity", "encounter")
        ),
        // ———————— OPUSZCZONY OBÓZ ————————————————————————
        Event(
            id = EV_ABANDONED_CAMP,
            context = EventContext.WILDERNESS,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 4,
            rootNodeId = EventNodeId("abandoned_camp_n1"),
            tags = setOf("discovery", "loot")
        ),
        // ———————— KUPIEC POD ATAKIEM ————————————————————————
        Event(
            id = EV_MERCHANT_ATTACK,
            context = EventContext.WILDERNESS,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 5,
            rootNodeId = EventNodeId("merchant_attack_n1"),
            tags = setOf("combat", "rescue")
        )
    )

    fun buildNodes(): List<EventNode> = listOf(
        // ═══════ BANDYCI ═══════════════════════════════════════════
        EventNode(
            id = EventNodeId("bandits_n1"),
            eventId = EV_BANDITS,
            textKey = "wild.bandits.desc",
            illustrationAsset = "bandits_road",
            options = listOf(
                EventOption(
                    id = EventOptionId("bandits_fight"),
                    textKey = "wild.bandits.fight",
                    outcome = StartCombatOutcome(
                        encounterId = "combat_bandits",
                        surpriseParty = false
                    )
                ),
                EventOption(
                    id = EventOptionId("bandits_negotiate"),
                    textKey = "wild.bandits.negotiate",
                    requirements = listOf(
                        SkillRequirement(HeroSkill.Speak, 50)
                    ),
                    outcome = ChainOutcome(listOf(
                        ModifyResourcesOutcome(goldDelta = -30),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("bandits_flee"),
                    textKey = "wild.bandits.flee",
                    outcome = ChainOutcome(listOf(
                        ModifyTimeOutcome(hours = 2),
                        EventEnd(EventEndResult.Escape)
                    ))
                )
            )
        ),
        // ═══════ WILKI ══════════════════════════════════════════════
        EventNode(
            id = EventNodeId("wolves_n1"),
            eventId = EV_WOLVES,
            textKey = "wild.wolves.desc",
            illustrationAsset = "wolves_night",
            options = listOf(
                EventOption(
                    id = EventOptionId("wolves_fight"),
                    textKey = "wild.wolves.fight",
                    outcome = StartCombatOutcome(
                        encounterId = "combat_wolves",
                        surpriseParty = false
                    )
                ),
                EventOption(
                    id = EventOptionId("wolves_fire"),
                    textKey = "wild.wolves.scare",
                    outcome = ChainOutcome(listOf(
                        ModifyTimeOutcome(hours = 1),
                        EventEnd(EventEndResult.Success)
                    ))
                )
            )
        ),
        // ═══════ PIELGRZYMI ══════════════════════════════════════════
        EventNode(
            id = EventNodeId("pilgrims_n1"),
            eventId = EV_PILGRIMS,
            textKey = "wild.pilgrims.desc",
            illustrationAsset = "pilgrims",
            options = listOf(
                EventOption(
                    id = EventOptionId("pilgrims_bless"),
                    textKey = "wild.pilgrims.bless",
                    outcome = ChainOutcome(listOf(
                        ModifyVirtueOutcome(mapOf(Virtue.Piety to 2)),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("pilgrims_leave"),
                    textKey = "wild.pilgrims.leave",
                    outcome = EventEnd(EventEndResult.Neutral)
                )
            )
        ),
        // ═══════ KARAWANA ═══════════════════════════════════════════
        EventNode(
            id = EventNodeId("caravan_n1"),
            eventId = EV_CARAVAN,
            textKey = "wild.caravan.desc",
            illustrationAsset = "caravan",
            options = listOf(
                EventOption(
                    id = EventOptionId("caravan_trade"),
                    textKey = "wild.caravan.trade",
                    outcome = EventGotoNode(EventNodeId("caravan_n2_trade"))
                ),
                EventOption(
                    id = EventOptionId("caravan_pass"),
                    textKey = "wild.caravan.pass",
                    outcome = EventEnd(EventEndResult.Neutral)
                )
            )
        ),
        EventNode(
            id = EventNodeId("caravan_n2_trade"),
            eventId = EV_CARAVAN,
            textKey = "wild.caravan.trade.desc",
            illustrationAsset = "caravan",
            options = listOf(
                EventOption(
                    id = EventOptionId("caravan_buy"),
                    textKey = "wild.caravan.buy",
                    outcome = ChainOutcome(listOf(
                        ModifyResourcesOutcome(goldDelta = -20),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("caravan_leave"),
                    textKey = "wild.caravan.leave",
                    outcome = EventEnd(EventEndResult.Neutral)
                )
            ),
        // ———————— PUSTELNIK ————————————————————————
        EventNode(
            id = EventNodeId("hermit_n1"),
            eventId = EV_HERMIT,
            textKey = "wild.hermit.encounter",
            illustrationAsset = "hermit_forest",
            options = listOf(
                EventOption(
                    id = EventOptionId("hermit_talk"),
                    textKey = "wild.hermit.talk",
                    outcome = ChainOutcome(listOf(
                        ModifyVirtueOutcome(virtueDelta = mapOf("Piety" to 5)),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("hermit_trade"),
                    textKey = "wild.hermit.trade",
                    outcome = ChainOutcome(listOf(
                        ModifyResourcesOutcome(goldDelta = -10),
                        EventEnd(EventEndResult.Neutral)
                    ))
                ),
                EventOption(
                    id = EventOptionId("hermit_leave"),
                    textKey = "wild.hermit.leave",
                    outcome = EventEnd(EventEndResult.Neutral)
                )
            )
        ),
        // ———————— UCHODŹCY ————————————————————————
        EventNode(
            id = EventNodeId("refugees_n1"),
            eventId = EV_REFUGEES,
            textKey = "wild.refugees.encounter",
            illustrationAsset = "refugees_road",
            options = listOf(
                EventOption(
                    id = EventOptionId("refugees_help"),
                    textKey = "wild.refugees.help",
                    outcome = ChainOutcome(listOf(
                        ModifyResourcesOutcome(goldDelta = -20),
                        ModifyVirtueOutcome(virtueDelta = mapOf("Charity" to 10)),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("refugees_ignore"),
                    textKey = "wild.refugees.ignore",
                    outcome = ChainOutcome(listOf(
                        ModifyVirtueOutcome(virtueDelta = mapOf("Charity" to -5)),
                        EventEnd(EventEndResult.Neutral)
                    ))
                )
            )
        ),
        // ———————— OPUSZCZONY OBÓZ ————————————————————————
        EventNode(
            id = EventNodeId("abandoned_camp_n1"),
            eventId = EV_ABANDONED_CAMP,
            textKey = "wild.abandoned_camp.discovery",
            illustrationAsset = "camp_ruins",
            options = listOf(
                EventOption(
                    id = EventOptionId("camp_search"),
                    textKey = "wild.camp.search",
                    outcome = ChainOutcome(listOf(
                        ModifyResourcesOutcome(goldDelta = 15),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("camp_trap"),
                    textKey = "wild.camp.trap",
                    requirements = listOf(PerceptionRequirement(15)),
                    outcome = ChainOutcome(listOf(
                        ModifyHealthOutcome(hpDeltaPerHero = -5),
                        EventEnd(EventEndResult.Failure)
                    ))
                ),
                EventOption(
                    id = EventOptionId("camp_leave"),
                    textKey = "wild.camp.leave",
                    outcome = EventEnd(EventEndResult.Neutral)
                )
            )
        ),
        // ———————— KUPIEC POD ATAKIEM ————————————————————————
        EventNode(
            id = EventNodeId("merchant_attack_n1"),
            eventId = EV_MERCHANT_ATTACK,
            textKey = "wild.merchant_attack.scene",
            illustrationAsset = "merchant_bandits",
            options = listOf(
                EventOption(
                    id = EventOptionId("merchant_rescue"),
                    textKey = "wild.merchant.rescue",
                    outcome = StartCombatOutcome(
                        encounterId = EncounterId("bandits_merchant_raid"),
                        onVictoryNode = EventNodeId("merchant_attack_reward")
                    )
                ),
                EventOption(
                    id = EventOptionId("merchant_avoid"),
                    textKey = "wild.merchant.avoid",
                    outcome = ChainOutcome(listOf(
                        ModifyVirtueOutcome(virtueDelta = mapOf("Honor" to -10)),
                        EventEnd(EventEndResult.Neutral)
                    ))
                )
            )
        ),
        EventNode(
            id = EventNodeId("merchant_attack_reward"),
            eventId = EV_MERCHANT_ATTACK,
            textKey = "wild.merchant.gratitude",
            illustrationAsset = "merchant_thanks",
            options = listOf(
                EventOption(
                    id = EventOptionId("merchant_accept_reward"),
                    textKey = "wild.merchant.accept",
                    outcome = ChainOutcome(listOf(
                        ModifyResourcesOutcome(goldDelta = 50),
                        ModifyVirtueOutcome(virtueDelta = mapOf("Honor" to 10)),
                        EventEnd(EventEndResult.Success)
                    ))
                )
            )
        )
        )
    )
}
