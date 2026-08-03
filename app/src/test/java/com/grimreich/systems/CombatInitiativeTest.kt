package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.world.ItemCatalogue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import kotlin.test.assertEquals

class CombatInitiativeTest {
    private lateinit var gameRepository: GameRepository
    private lateinit var combatSystem: CombatSystem
    private val randomProvider = mock<CombatRandomProvider>()
    private lateinit var combatRound: CombatRound
    private val lootSystem = mock<LootSystem>()
    private val itemCatalogue = mock<ItemCatalogue>()

    @Before
    fun setup() {
        gameRepository = mock()
        whenever(lootSystem.itemCatalogue).thenReturn(itemCatalogue)
        combatRound = CombatRound(gameRepository, mock(), randomProvider)
        combatSystem = CombatSystem(
            gameRepository = gameRepository,
            moraleSystem = mock(),
            combatRound = combatRound,
            questEngine = mock(),
            experienceSystem = mock(),
            lootSystem = lootSystem
        )
    }

    @Test
    fun `test advanceTurn round increment logic`() {
        val state = GameState()
        state.combat.active = true
        state.combat.round = 1
        state.combat.initiativeOrder.addAll(listOf(
            InitiativeSlot("h1", true, 10),
            InitiativeSlot("enemy", false, 5)
        ))
        state.combat.currentTurnIndex = 0

        // Round 1, Index 0 -> Index 1
        val advanceTurn = combatSystem.javaClass.getDeclaredMethod("advanceTurn", GameState::class.java).apply {
            isAccessible = true
        }
        
        advanceTurn.invoke(combatSystem, state)
        assertEquals(1, state.combat.currentTurnIndex)
        assertEquals(1, state.combat.round)

        // Round 1, Index 1 -> Index 0 (Wrap around)
        advanceTurn.invoke(combatSystem, state)
        assertEquals(0, state.combat.currentTurnIndex)
        assertEquals(2, state.combat.round)
    }

    @Test
    fun `test hero death index adjustment`() {
        val state = GameState()
        state.combat.active = true
        state.combat.initiativeOrder.addAll(listOf(
            InitiativeSlot("h1", true, 20),
            InitiativeSlot("h2", true, 15),
            InitiativeSlot("enemy", false, 10)
        ))
        
        val h1 = Hero(id = "h1", name = "H1", hp = 10, maxHp = 10)
        val h2 = Hero(id = "h2", name = "H2", hp = 10, maxHp = 10)
        state.party.addAll(listOf(h1, h2))

        // Scenario 1: h1 dies while it is h1's turn (index 0)
        state.combat.currentTurnIndex = 0
        val handleHeroDeath = combatSystem.javaClass.getDeclaredMethod("handleHeroDeath", GameState::class.java, Hero::class.java).apply {
            isAccessible = true
        }
        handleHeroDeath.invoke(combatSystem, state, h1)
        
        // After death of index 0, currentTurnIndex should be -1
        // so that advanceTurn brings it to 0 (which is now h2).
        assertEquals(-1, state.combat.currentTurnIndex)
        assertEquals(2, state.combat.initiativeOrder.size)
        assertEquals("h2", state.combat.initiativeOrder[0].id)

        val advanceTurn = combatSystem.javaClass.getDeclaredMethod("advanceTurn", GameState::class.java).apply {
            isAccessible = true
        }
        advanceTurn.invoke(combatSystem, state)
        assertEquals(0, state.combat.currentTurnIndex)
        assertEquals("h2", state.combat.initiativeOrder[state.combat.currentTurnIndex].id)
    }

    @Test
    fun `test round increment with single combatant`() {
        val state = GameState()
        state.combat.active = true
        state.combat.round = 1
        state.combat.initiativeOrder.add(InitiativeSlot("h1", true, 10))
        state.combat.currentTurnIndex = 0

        val advanceTurn = combatSystem.javaClass.getDeclaredMethod("advanceTurn", GameState::class.java).apply {
            isAccessible = true
        }
        
        advanceTurn.invoke(combatSystem, state)
        
        assertEquals(0, state.combat.currentTurnIndex)
        assertEquals(2, state.combat.round)
    }
}
