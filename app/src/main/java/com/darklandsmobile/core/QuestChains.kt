package com.darklandsmobile.core

/**
 * Pełne łańcuchy questów (Raubritter, Endgame/Kult) z Darklands:
 * definicje stageów, powiązane eventy, nagrody, warunki postępu.
 */

// ────────── QUEST DEFINITION ───────────────────────────────────────────────

data class QuestDefinition(
    val id: String,
    val name: String,
    val description: String,
    val stages: List<QuestStage>,
    val rewards: QuestRewards
)

data class QuestStage(
    val id: String,
    val name: String,
    val description: String,
    val objectives: List<QuestObjective>,
    val triggerEvents: List<EventId> = emptyList()
)

sealed interface QuestObjective {
    val description: String
}

data class DefeatEnemyObjective(
    override val description: String,
    val encounterId: String
) : QuestObjective

data class VisitLocationObjective(
    override val description: String,
    val locationId: String
) : QuestObjective

data class TalkToNPCObjective(
    override val description: String,
    val npcId: String
) : QuestObjective

data class CollectItemObjective(
    override val description: String,
    val item: Item,
    val amount: Int
) : QuestObjective

data class QuestRewards(
    val gold: Int = 0,
    val items: List<Item> = emptyList(),
    val reputationChanges: Map<Faction, Int> = emptyMap(),
    val xp: Int = 0
)

// ────────── RAUBRITTER QUEST CHAIN ──────────────────────────────────────

object RaubritterQuestChain {
    val quest = QuestDefinition(
        id = "quest_raubritter",
        name = "Raubritter",
        description = "Zbrojny rycerz terroryzuje okoliczne wioski. Miejscowe władze proszą o pomoc.",
        stages = listOf(
            QuestStage(
                id = "rumor",
                name = "Plotki o raubritterze",
                description = "Usłysz plotki o raubritterze w miejscowej karczmie.",
                objectives = listOf(
                    TalkToNPCObjective(
                        description = "Porozmawiaj z karczmarzem",
                        npcId = "innkeeper_city_01"
                    )
                ),
                triggerEvents = listOf(EventId("ev_rumor_raubritter"))
            ),
            QuestStage(
                id = "active",
                name = "Poszukiwania",
                description = "Odnajdź zamek raubrittera i pokonaj jego zwiadowców.",
                objectives = listOf(
                    DefeatEnemyObjective(
                        description = "Pokonaj zwiadowców raubrittera",
                        encounterId = "combat_raubritter_scouts"
                    ),
                    VisitLocationObjective(
                        description = "Odnajdź zamek raubrittera",
                        locationId = "raubritter_castle"
                    )
                ),
                triggerEvents = listOf(EventId("ev_wild_scouts"))
            ),
            QuestStage(
                id = "final",
                name = "Starcie",
                description = "Wejdź do zamku i pokonaj raubrittera.",
                objectives = listOf(
                    DefeatEnemyObjective(
                        description = "Pokonaj raubrittera",
                        encounterId = "combat_raubritter_boss"
                    )
                ),
                triggerEvents = listOf(EventId("ev_dung_raubritter_hall"))
            ),
            QuestStage(
                id = "completed",
                name = "Zwycięstwo",
                description = "Raubritter został pokonany. Wróć do miasta po nagrodę.",
                objectives = emptyList()
            )
        ),
        rewards = QuestRewards(
            gold = 500,
            reputationChanges = mapOf(
                Faction.Locals to 30,
                Faction.Church to 15
            ),
            xp = 150
        )
    )
}

// ────────── ENDGAME / KULT BAPHOMETA ─────────────────────────────────

object EndgameQuestChain {
    val quest = QuestDefinition(
        id = "quest_endgame",
        name = "Kult Baphometa",
        description = "Tajemniczy kult demonologów planuje sprowadzenie potwornego demona. Tylko Ty możesz ich powstrzymać.",
        stages = listOf(
            QuestStage(
                id = "discover",
                name = "Odkrycie",
                description = "Dowiedz się o istnieniu kultu.",
                objectives = listOf(
                    TalkToNPCObjective(
                        description = "Porozmawiaj z mędrcem",
                        npcId = "sage_city_cathedral"
                    )
                ),
                triggerEvents = listOf(EventId("ev_city_sage_warning"))
            ),
            QuestStage(
                id = "investigate",
                name = "śledztwo",
                description = "Zbadaj nocne obrzędy kultu na pustkowiu.",
                objectives = listOf(
                    DefeatEnemyObjective(
                        description = "Przerwij rytuał kultystów",
                        encounterId = "combat_cultists"
                    )
                ),
                triggerEvents = listOf(EventId("ev_wild_cult"))
            ),
            QuestStage(
                id = "cult_location",
                name = "Kryjówka kultu",
                description = "Odnajdź główną siedzibę kultu.",
                objectives = listOf(
                    VisitLocationObjective(
                        description = "Odnajdź podziemną świątynię",
                        locationId = "cult_temple"
                    )
                )
            ),
            QuestStage(
                id = "cult_defeated",
                name = "Rozprawa",
                description = "Pokonaj głównego kapłana kultu i powstrzymaj przyzwanie.",
                objectives = listOf(
                    DefeatEnemyObjective(
                        description = "Pokonaj głównego demonologa",
                        encounterId = "combat_demon_summoner"
                    )
                ),
                triggerEvents = listOf(EventId("ev_dung_cultists"))
            ),
            QuestStage(
                id = "final_demon",
                name = "Ostateczna rozprawa",
                description = "Przyzwany demon atakuje! To ostatnia szansa.",
                objectives = listOf(
                    DefeatEnemyObjective(
                        description = "Pokonaj wielkiego demona",
                        encounterId = "combat_demon_major"
                    )
                )
            ),
            QuestStage(
                id = "completed",
                name = "Wybawienie",
                description = "Kult został zniszczony, a demon pokonany. Świat jest bezpieczny.",
                objectives = emptyList()
            )
        ),
        rewards = QuestRewards(
            gold = 2000,
            reputationChanges = mapOf(
                Faction.Church to 50,
                Faction.Locals to 40
            ),
            xp = 500
        )
    )
}

// ────────── QUEST CATALOG ─────────────────────────────────────────────────

object QuestCatalog {
    private val quests = mapOf(
        "quest_raubritter" to RaubritterQuestChain.quest,
        "quest_endgame" to EndgameQuestChain.quest
    )

    fun get(id: String): QuestDefinition? = quests[id]
    fun all(): List<QuestDefinition> = quests.values.toList()
}
