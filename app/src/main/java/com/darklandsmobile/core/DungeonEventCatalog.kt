package com.darklandsmobile.core

/**
 * Katalog eventów DUNGEON (lochy, zamki, kryjówki kultu) na podstawie Darklands.
 * Obejmuje: pułapki, szkielety, kultystów, kaplice, skarbce, zamki raubritteró, zamknięte drzwi, więźnia, sekretne przejścia.
 */

object DungeonEventCatalog {

    private val EV_TRAP_POISON = EventId("ev_dung_trap_poison")
    private val EV_UNDEAD = EventId("ev_dung_undead")
    private val EV_CULTISTS = EventId("ev_dung_cultists")
    private val EV_SHRINE = EventId("ev_dung_shrine")
    private val EV_TREASURE = EventId("ev_dung_treasure")
    private val EV_RAUBRITTER_HALL = EventId("ev_dung_raubritter_hall")
        private val EV_LOCKED_DOOR = EventId("ev_dung_locked_door")
            private val EV_PRISONER = EventId("ev_dung_prisoner")
                private val EV_SECRET_PASSAGE = EventId("ev_dung_secret_passage")

    fun buildEvents(): List<Event> = listOf(
        // ────── PUŁAPKA TRUCIZNOWA ─────────────────────────────────
        Event(
            id = EV_TRAP_POISON,
            context = EventContext.DUNGEON,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 5,
            rootNodeId = EventNodeId("trap_poison_n1"),
            tags = setOf("trap", "danger")
        ),
        // ────── SZKIELETY / UMARŁI ──────────────────────────────────
        Event(
            id = EV_UNDEAD,
            context = EventContext.DUNGEON,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 6,
            rootNodeId = EventNodeId("undead_n1"),
            tags = setOf("combat", "undead")
        ),
        // ────── KULTYSTA / DEMONOLOG ────────────────────────────────
        Event(
            id = EV_CULTISTS,
            context = EventContext.DUNGEON,
            category = EventCategory.STORY,
            weight = 4,
            rootNodeId = EventNodeId("cultists_n1"),
            tags = setOf("combat", "cult", "endgame")
        ),
        // ────── KAPLICZKA / OŁTARZ ────────────────────────────────────
        Event(
            id = EV_SHRINE,
            context = EventContext.DUNGEON,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 2,
            rootNodeId = EventNodeId("shrine_n1"),
            tags = setOf("religion", "mystery")
        ),
        // ────── SKARBIEC ────────────────────────────────────────────
        Event(
            id = EV_TREASURE,
            context = EventContext.DUNGEON,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 3,
            rootNodeId = EventNodeId("treasure_n1"),
            tags = setOf("loot", "reward")
        ),
        // ────── ZAMEK RAUBRITTERA ───────────────────────────────────
        Event(
            id = EV_RAUBRITTER_HALL,
            context = EventContext.DUNGEON,
            category = EventCategory.QUEST,
            weight = 1,
            rootNodeId = EventNodeId("raubritter_hall_n1"),
            conditions = listOf(
                QuestStateCondition("quest_raubritter", "final")
            ),
            tags = setOf("quest", "boss")
        ),
        // ———————— ZAMKNIĘTE DRZWI ————————————————————————
        Event(
            id = EV_LOCKED_DOOR,
            context = EventContext.DUNGEON,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 5,
            rootNodeId = EventNodeId("locked_door_n1"),
            tags = setOf("puzzle", "trap")
        ),
        // ———————— WIĘZIEŃ ————————————————————————
        Event(
            id = EV_PRISONER,
            context = EventContext.DUNGEON,
            category = EventCategory.STORY,
            weight = 3,
            rootNodeId = EventNodeId("prisoner_n1"),
            tags = setOf("rescue", "choice")
        ),
        // ———————— SEKRETNE PRZEJŚCIE ————————————————————————
        Event(
            id = EV_SECRET_PASSAGE,
            context = EventContext.DUNGEON,
            category = EventCategory.RANDOM_ENCOUNTER,
            weight = 4,
            rootNodeId = EventNodeId("secret_passage_n1"),
            tags = setOf("discovery", "shortcut")
        )
    )

    fun buildNodes(): List<EventNode> = listOf(
        // ═══════ PUŁAPKA ════════════════════════════════════════════
        EventNode(
            id = EventNodeId("trap_poison_n1"),
            eventId = EV_TRAP_POISON,
            textKey = "dung.trap.poison.desc",
            illustrationAsset = "trap_poison",
            options = listOf(
                EventOption(
                    id = EventOptionId("trap_disarm"),
                    textKey = "dung.trap.disarm",
                    requirements = listOf(
                        SkillRequirement(HeroSkill.Artifice, 60)
                    ),
                    outcome = EventEnd(EventEndResult.Success)
                ),
                EventOption(
                    id = EventOptionId("trap_trigger"),
                    textKey = "dung.trap.trigger",
                    outcome = ChainOutcome(listOf(
                        ModifyHealthOutcome(hpDeltaPerHero = -15),
                        EventEnd(EventEndResult.Failure)
                    ))
                )
            )
        ),
        // ═══════ SZKIELETY ═══════════════════════════════════════════
        EventNode(
            id = EventNodeId("undead_n1"),
            eventId = EV_UNDEAD,
            textKey = "dung.undead.desc",
            illustrationAsset = "undead",
            options = listOf(
                EventOption(
                    id = EventOptionId("undead_fight"),
                    textKey = "dung.undead.fight",
                    outcome = StartCombatOutcome(
                        encounterId = "combat_undead",
                        surpriseParty = false
                    )
                ),
                EventOption(
                    id = EventOptionId("undead_prayer"),
                    textKey = "dung.undead.prayer",
                    requirements = listOf(
                        VirtueRequirement(Virtue.Piety, 50)
                    ),
                    outcome = ChainOutcome(listOf(
                        ModifyVirtueOutcome(mapOf(Virtue.Piety to -5)),
                        EventEnd(EventEndResult.Success)
                    ))
                )
            )
        ),
        // ═══════ KULTYSTA ═══════════════════════════════════════════
        EventNode(
            id = EventNodeId("cultists_n1"),
            eventId = EV_CULTISTS,
            textKey = "dung.cultists.desc",
            illustrationAsset = "cultists",
            options = listOf(
                EventOption(
                    id = EventOptionId("cultists_fight"),
                    textKey = "dung.cultists.fight",
                    outcome = ChainOutcome(listOf(
                        StartCombatOutcome(
                            encounterId = "combat_cultists",
                            surpriseParty = false
                        ),
                        AdvanceQuestOutcome("quest_endgame", "cult_defeated")
                    ))
                ),
                EventOption(
                    id = EventOptionId("cultists_stealth"),
                    textKey = "dung.cultists.stealth",
                    requirements = listOf(
                        SkillRequirement(HeroSkill.Stealth, 70)
                    ),
                    outcome = EventEnd(EventEndResult.Success)
                )
            )
        ),
        // ═══════ KAPLICZKA ══════════════════════════════════════════
        EventNode(
            id = EventNodeId("shrine_n1"),
            eventId = EV_SHRINE,
            textKey = "dung.shrine.desc",
            illustrationAsset = "shrine",
            options = listOf(
                EventOption(
                    id = EventOptionId("shrine_pray"),
                    textKey = "dung.shrine.pray",
                    outcome = ChainOutcome(listOf(
                        ModifyVirtueOutcome(mapOf(Virtue.Piety to 3)),
                        ModifyHealthOutcome(hpDeltaPerHero = 10),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("shrine_leave"),
                    textKey = "dung.shrine.leave",
                    outcome = EventEnd(EventEndResult.Neutral)
                )
            )
        ),
        // ═══════ SKARBIEC ═══════════════════════════════════════════
        EventNode(
            id = EventNodeId("treasure_n1"),
            eventId = EV_TREASURE,
            textKey = "dung.treasure.desc",
            illustrationAsset = "treasure_room",
            options = listOf(
                EventOption(
                    id = EventOptionId("treasure_take"),
                    textKey = "dung.treasure.take",
                    outcome = ChainOutcome(listOf(
                        ModifyResourcesOutcome(goldDelta = 150),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("treasure_trap_check"),
                    textKey = "dung.treasure.check_trap",
                    requirements = listOf(
                        SkillRequirement(HeroSkill.Artifice, 40)
                    ),
                    outcome = EventGotoNode(EventNodeId("treasure_n2_safe"))
                )
            )
        ),
        EventNode(
            id = EventNodeId("treasure_n2_safe"),
            eventId = EV_TREASURE,
            textKey = "dung.treasure.safe.desc",
            illustrationAsset = "treasure_room",
            options = listOf(
                EventOption(
                    id = EventOptionId("treasure_take_safe"),
                    textKey = "dung.treasure.take",
                    outcome = ChainOutcome(listOf(
                        ModifyResourcesOutcome(goldDelta = 200),
                        EventEnd(EventEndResult.Success)
                    ))
                )
            )
        ),
        // ═══════ RAUBRITTER BOSS ═════════════════════════════════════
        EventNode(
            id = EventNodeId("raubritter_hall_n1"),
            eventId = EV_RAUBRITTER_HALL,
            textKey = "dung.raubritter.hall.desc",
            illustrationAsset = "raubritter_hall",
            options = listOf(
                EventOption(
                    id = EventOptionId("raubritter_fight"),
                    textKey = "dung.raubritter.fight",
                    outcome = ChainOutcome(listOf(
                        StartCombatOutcome(
                            encounterId = "combat_raubritter_boss",
                            surpriseParty = false
                        ),
                        AdvanceQuestOutcome("quest_raubritter", "completed"),
                        ModifyReputationOutcome(mapOf(
                            Faction.Locals to 20,
                            Faction.Church to 10
                        ))
                    ))
                )
            ),
        // ———————— ZAMKNIĘTE DRZWI ————————————————————————
        EventNode(
            id = EventNodeId("locked_door_n1"),
            eventId = EV_LOCKED_DOOR,
            textKey = "dung.locked_door.discovery",
            illustrationAsset = "door_locked",
            options = listOf(
                EventOption(
                    id = EventOptionId("door_lockpick"),
                    textKey = "dung.door.lockpick",
                    requirements = listOf(SkillRequirement("Lockpick", 20)),
                    outcome = ChainOutcome(listOf(
                        ModifyTimeOutcome(minutesDelta = 15),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("door_bash"),
                    textKey = "dung.door.bash",
                    requirements = listOf(AttributeRequirement("Strength", 15)),
                    outcome = ChainOutcome(listOf(
                        ModifyHealthOutcome(hpDeltaPerHero = -10),
                        EventEnd(EventEndResult.Neutral)
                    ))
                ),
                EventOption(
                    id = EventOptionId("door_find_key"),
                    textKey = "dung.door.search_key",
                    outcome = ChainOutcome(listOf(
                        ModifyTimeOutcome(minutesDelta = 30),
                        EventEnd(EventEndResult.Success)
                    ))
                )
            )
        ),
        // ———————— WIĘZIEŃ ————————————————————————
        EventNode(
            id = EventNodeId("prisoner_n1"),
            eventId = EV_PRISONER,
            textKey = "dung.prisoner.encounter",
            illustrationAsset = "cell_prisoner",
            options = listOf(
                EventOption(
                    id = EventOptionId("prisoner_free"),
                    textKey = "dung.prisoner.free",
                    outcome = ChainOutcome(listOf(
                        ModifyVirtueOutcome(virtueDelta = mapOf("Charity" to 15)),
                        ModifyReputationOutcome(factionRepDelta = mapOf("Locals" to 10)),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("prisoner_question"),
                    textKey = "dung.prisoner.question",
                    outcome = ChainOutcome(listOf(
                        ModifyTimeOutcome(minutesDelta = 10),
                        EventEnd(EventEndResult.Neutral)
                    ))
                ),
                EventOption(
                    id = EventOptionId("prisoner_leave"),
                    textKey = "dung.prisoner.leave",
                    outcome = ChainOutcome(listOf(
                        ModifyVirtueOutcome(virtueDelta = mapOf("Charity" to -10)),
                        EventEnd(EventEndResult.Neutral)
                    ))
                )
            )
        ),
        // ———————— SEKRETNE PRZEJŚCIE ————————————————————————
        EventNode(
            id = EventNodeId("secret_passage_n1"),
            eventId = EV_SECRET_PASSAGE,
            textKey = "dung.secret_passage.discovery",
            illustrationAsset = "passage_hidden",
            options = listOf(
                EventOption(
                    id = EventOptionId("passage_take"),
                    textKey = "dung.passage.take",
                    outcome = ChainOutcome(listOf(
                        ModifyTimeOutcome(minutesDelta = -30),
                        EventEnd(EventEndResult.Success)
                    ))
                ),
                EventOption(
                    id = EventOptionId("passage_ignore"),
                    textKey = "dung.passage.ignore",
                    outcome = EventEnd(EventEndResult.Neutral)
                )
            )
        )
        )
    )
}
