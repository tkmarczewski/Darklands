package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.ui.main.ExpeditionViewModel
import com.grimreich.world.CityCatalogue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EncounterLogicHardeningTest {
    private lateinit var viewModel: ExpeditionViewModel
    private lateinit var gameRepository: GameRepository
    private val state = GameState()
    private val testDispatcher = UnconfinedTestDispatcher()

    // Mocks
    private val questEngine = mock<QuestEngine>()
    private val cityCatalogue = mock<CityCatalogue>()
    private val encounterSystem = mock<EncounterSystem>()
    private val combatSystem = mock<CombatSystem>()
    private val random = mock<CombatRandomProvider>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gameRepository = mock()
        whenever(gameRepository.gameState).thenReturn(MutableStateFlow(state))
        whenever(gameRepository.currentState()).thenReturn(state)
        
        // Mock updateState
        whenever(gameRepository.updateState(any<Boolean>(), any())).thenAnswer { invocation ->
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(state)
            null
        }

        viewModel = ExpeditionViewModel(
            gameRepository, questEngine, cityCatalogue, encounterSystem, combatSystem, random
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `handleEncounterChoice should block effect if requirements not met`() {
        var effectExecuted = false
        val choice = EncounterChoice(
            label = "Test",
            description = "Desc",
            requiredAttribute = "strength",
            requiredValue = 20,
            effect = { 
                effectExecuted = true
                "Success"
            }
        )

        // Hero has 10 strength (base default)
        val hero = Hero(id = "h1", strength = 10)
        state.party.add(hero)
        state.activeHeroId = hero.id

        viewModel.onEvent(com.grimreich.ui.main.ExpeditionUiEvent.OnEncounterChoiceClick(choice))

        assertFalse(effectExecuted, "Effect should not have been executed because strength is too low")
    }

    @Test
    fun `handleEncounterChoice should execute effect if requirements met`() {
        var effectExecuted = false
        val choice = EncounterChoice(
            label = "Test",
            description = "Desc",
            requiredAttribute = "strength",
            requiredValue = 15,
            effect = { 
                effectExecuted = true
                "Success"
            }
        )

        val hero = Hero(id = "h1", strength = 18)
        state.party.add(hero)
        state.activeHeroId = hero.id

        viewModel.onEvent(com.grimreich.ui.main.ExpeditionUiEvent.OnEncounterChoiceClick(choice))

        assertTrue(effectExecuted, "Effect should have been executed because strength is sufficient")
    }
}
