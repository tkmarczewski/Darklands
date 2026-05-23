package com.darklandsmobile.core

// ─── Wynik przetwarzania outcome ─────────────────────────────────────────────

data class OutcomeResult(
    val gameState: GameState,
    val nextNodeId: EventNodeId? = null,
    val endResult: EventEndResult? = null
)

// ─── Główny applier ───────────────────────────────────────────────────────────

object EventOutcomeApplier {

    fun apply(
        outcome: EventOutcome,
        gameState: GameState,
        worldState: WorldState,
        questGraph: QuestGraph
    ): OutcomeResult = when (outcome) {

        // — nawigacja w dialogu
        is EventGotoNode -> OutcomeResult(
            gameState = gameState,
            nextNodeId = outcome.nodeId
        )

        is EventEnd -> OutcomeResult(
            gameState = gameState,
            endResult = outcome.result
        )

        // — reputacja
        is ModifyReputationOutcome -> {
            val newRep = outcome.factionChanges.entries.fold(gameState.reputationState) { rep, (faction, delta) ->
                rep.applyChange(faction, delta)
            }
            OutcomeResult(gameState.copy(reputationState = newRep))
        }

        // — cnoty
        is ModifyVirtueOutcome -> {
            val newParty = gameState.party.copy(
                members = gameState.party.members.map { hero ->
                    outcome.virtueChanges.entries.fold(hero) { h, (virtue, delta) ->
                        h.copy(virtues = h.virtues.applyChange(virtue, delta))
                    }
                }
            )
            OutcomeResult(gameState.copy(party = newParty))
        }

        // — złoto i ekwipunek
        is ModifyResourcesOutcome -> {
            val newGold = (gameState.gold + outcome.goldDelta).coerceAtLeast(0)
            var newGameState = gameState.copy(gold = newGold)
            outcome.itemChanges.forEach { change ->
                newGameState = applyItemChange(newGameState, change)
            }
            OutcomeResult(newGameState)
        }

        // — zdrowie
        is ModifyHealthOutcome -> {
            val newParty = gameState.party.copy(
                members = gameState.party.members.map { hero ->
                    val newHp = (hero.currentHp + outcome.hpDeltaPerHero)
                        .coerceAtLeast(if (outcome.canKill) 0 else 1)
                        .coerceAtMost(hero.maxHp)
                    hero.copy(currentHp = newHp)
                }
            )
            OutcomeResult(gameState.copy(party = newParty))
        }

        // — umiejętności
        is ModifySkillOutcome -> {
            val newParty = when (outcome.target) {
                RequirementTarget.AnyHero -> {
                    val idx = gameState.party.members
                        .indexOfFirst { it.skills.getLevel(outcome.skill) > 0 }
                        .takeIf { it >= 0 } ?: 0
                    gameState.party.copy(
                        members = gameState.party.members.mapIndexed { i, hero ->
                            if (i == idx) hero.copy(
                                skills = hero.skills.applyChange(outcome.skill, outcome.delta)
                            ) else hero
                        }
                    )
                }
                RequirementTarget.PartyLeader -> gameState.party.copy(
                    members = gameState.party.members.mapIndexed { i, hero ->
                        if (i == 0) hero.copy(
                            skills = hero.skills.applyChange(outcome.skill, outcome.delta)
                        ) else hero
                    }
                )
                RequirementTarget.SpecificHeroIndex -> gameState.party
            }
            OutcomeResult(gameState.copy(party = newParty))
        }

        // — upływ czasu
        is ModifyTimeOutcome -> {
            val newWorld = worldState.advanceTime(outcome.hours)
            OutcomeResult(gameState.copy(worldState = newWorld))
        }

        // — zmiana stanu świata / teleportacja
        is ModifyWorldStateOutcome -> {
            var newWorld = worldState
            if (outcome.locationId != null) {
                newWorld = newWorld.moveTo(outcome.locationId)
            }
            if (outcome.flagsToSet.isNotEmpty()) {
                newWorld = newWorld.setFlags(outcome.flagsToSet)
            }
            OutcomeResult(gameState.copy(worldState = newWorld))
        }

        // — walka
        is StartCombatOutcome -> {
            val newGameState = gameState.copy(
                pendingCombatEncounterId = outcome.encounterId,
                combatSurpriseParty = outcome.surpriseParty,
                combatSurpriseEnemies = outcome.surpriseEnemies
            )
            OutcomeResult(newGameState, endResult = EventEndResult.Neutral)
        }

        // — quest start
        is StartQuestOutcome -> {
            val newQuestState = gameState.questState.startQuest(outcome.questId)
            OutcomeResult(gameState.copy(questState = newQuestState))
        }

        // — quest advance
        is AdvanceQuestOutcome -> {
            val newQuestState = gameState.questState.advanceTo(
                outcome.questId,
                outcome.nextStage
            )
            OutcomeResult(gameState.copy(questState = newQuestState))
        }

        // — chain: stosujemy po kolei, zbieramy ostatni nextNodeId i endResult
        is ChainOutcome -> {
            var currentState = gameState
            var lastNextNode: EventNodeId? = null
            var lastEnd: EventEndResult? = null

            outcome.outcomes.forEach { o ->
                val res = apply(o, currentState, worldState, questGraph)
                currentState = res.gameState
                if (res.nextNodeId != null) lastNextNode = res.nextNodeId
                if (res.endResult != null) lastEnd = res.endResult
            }

            OutcomeResult(currentState, lastNextNode, lastEnd)
        }
    }

    // ─── Pomocnicza: zmiana ekwipunku ────────────────────────────────────────

    private fun applyItemChange(
        gameState: GameState,
        change: ItemChange
    ): GameState {
        return when (change.target) {
            InventoryTarget.PartyStash -> {
                val updatedStash = gameState.partyStash.applyItemDelta(change.item, change.delta)
                gameState.copy(partyStash = updatedStash)
            }
            InventoryTarget.RandomHero -> {
                val idx = gameState.party.members.indices.random()
                val updatedMembers = gameState.party.members.mapIndexed { i, hero ->
                    if (i == idx) hero.copy(
                        inventory = hero.inventory.applyItemDelta(change.item, change.delta)
                    ) else hero
                }
                gameState.copy(party = gameState.party.copy(members = updatedMembers))
            }
            InventoryTarget.SpecificHero -> {
                // fallback do stashu jeśli brak konkretnego indeksu
                val updatedStash = gameState.partyStash.applyItemDelta(change.item, change.delta)
                gameState.copy(partyStash = updatedStash)
            }
        }
    }
}
