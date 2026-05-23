package com.darklandsmobile.core

// ─── Interfejs repozytorium eventów ──────────────────────────────────────────

interface EventRepository {
    fun getEvent(eventId: EventId): Event
    fun getNode(nodeId: EventNodeId): EventNode
    fun eventsForContext(context: EventContext): List<Event>
}

// ─── Implementacja in-memory ─────────────────────────────────────────────────

class InMemoryEventRepository(
    private val events: Map<EventId, Event>,
    private val nodes: Map<EventNodeId, EventNode>
) : EventRepository {

    override fun getEvent(eventId: EventId): Event =
        events[eventId] ?: error("Unknown event: ${eventId.value}")

    override fun getNode(nodeId: EventNodeId): EventNode =
        nodes[nodeId] ?: error("Unknown node: ${nodeId.value}")

    override fun eventsForContext(context: EventContext): List<Event> =
        events.values.filter { it.context == context }
}

// ─── EventService — integracja z GameState/Travel/Quest ────────────────────

class EventService(
    private val eventRepository: EventRepository,
    private val gameRepository: GameRepository,
    private val questGraph: QuestGraph
) {

    fun pickRandomEvent(context: EventContext): Event? {
        val gameState = gameRepository.loadGameState()
        val worldState = gameState.worldState

        val candidates = eventRepository
            .eventsForContext(context)
            .filter { event ->
                event.conditions.all { it.isSatisfied(gameState, worldState) }
            }

        if (candidates.isEmpty()) return null
        return weightedRandom(candidates) { it.weight }
    }

    fun startEvent(eventId: EventId): ActiveEvent {
        val event = eventRepository.getEvent(eventId)
        val rootNode = eventRepository.getNode(event.rootNodeId)
        return ActiveEvent(event, rootNode)
    }

    fun getVisibleOptions(
        activeEvent: ActiveEvent,
        party: Party,
        gameState: GameState
    ): List<EventOption> {
        return activeEvent.currentNode.options.filter { option ->
            option.requirements.all { it.isMet(party, gameState) }
        }
    }

    fun applyOption(
        activeEvent: ActiveEvent,
        optionId: EventOptionId
    ): ActiveEventResult {
        val option = activeEvent.currentNode.options
            .firstOrNull { it.id == optionId }
            ?: error("Unknown option: ${optionId.value}")

        val gameState = gameRepository.loadGameState()
        val worldState = gameState.worldState

        val result = EventOutcomeApplier.apply(
            option.outcome,
            gameState,
            worldState,
            questGraph
        )

        gameRepository.saveGameState(result.gameState)

        return if (result.nextNodeId == null) {
            ActiveEventResult.Finished(result.endResult ?: EventEndResult.Neutral)
        } else {
            val nextNode = eventRepository.getNode(result.nextNodeId)
            ActiveEventResult.Continue(activeEvent.copy(currentNode = nextNode))
        }
    }

    private fun <T> weightedRandom(list: List<T>, weight: (T) -> Int): T {
        val total = list.sumOf { weight(it) }
        val r = (1..total).random()
        var acc = 0
        for (e in list) {
            acc += weight(e)
            if (r <= acc) return e
        }
        return list.last()
    }
}
