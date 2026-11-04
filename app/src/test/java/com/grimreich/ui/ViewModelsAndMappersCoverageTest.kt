package com.grimreich.ui

import com.grimreich.core.*
import com.grimreich.grimreich.v1.Item
import com.grimreich.systems.*
import com.grimreich.ui.character.CharacterHubUiMapper
import com.grimreich.ui.character.CharacterHubViewModel
import com.grimreich.ui.city.CityUiEvent
import com.grimreich.ui.city.CityViewModel
import com.grimreich.ui.combat.CombatViewModel
import com.grimreich.ui.inventory.InventoryViewModel
import com.grimreich.ui.main.*
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import com.grimreich.world.ProceduralNpcGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsAndMappersCoverageTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var gameRepository: GameRepository
    private lateinit var state: GameState
    private lateinit var questEngine: QuestEngine
    private lateinit var cityCatalogue: CityCatalogue
    private lateinit var itemCatalogue: ItemCatalogue

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        gameRepository = mock()
        state = GameState()
        whenever(gameRepository.currentState()).thenReturn(state)
        whenever(gameRepository.gameState).thenReturn(kotlinx.coroutines.flow.MutableStateFlow(state))
        whenever(gameRepository.gameLogs).thenReturn(kotlinx.coroutines.flow.MutableStateFlow(emptyList()))

        whenever(gameRepository.updateState(anyBoolean(), any())).thenAnswer { invocation ->
            val block = invocation.getArgument<(GameState) -> Unit>(1)
            block(state)
            null
        }

        itemCatalogue = ItemCatalogue().apply { seed() }
        cityCatalogue = CityCatalogue().apply { seedCanonical() }

        val expSystem = mock<ExperienceSystem>()
        whenever(expSystem.addPartyXpDirect(any(), any())).thenReturn(emptyList())

        questEngine = QuestEngine(
            context = mock(),
            gameRepositoryProvider = { gameRepository },
            experienceSystemProvider = { expSystem }
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testCharacterHubUiMapper() {
        val mockContext = mock<android.content.Context>()
        val mockResources = mock<android.content.res.Resources>()
        whenever(mockContext.resources).thenReturn(mockResources)
        whenever(mockContext.packageName).thenReturn("com.grimreich")

        val mapper = CharacterHubUiMapper(mockContext)
        state.party.add(Hero(id = "h1", name = "TestHero", hp = 30, maxHp = 50, strength = 14, agility = 12))

        val uiState = mapper.map(state)
        assertEquals(1, uiState.heroes.size)
        assertEquals("TestHero", uiState.heroes[0].name)
    }

    @Test
    fun testCityViewModelEvents() {
        val npcGenerator = mock<ProceduralNpcGenerator>()
        val socialSystem = mock<SocialEventSystem>()
        val dialogueManager = mock<DialogueManager>()
        val atmosphericSystem = mock<AtmosphericDescriptionSystem>()
        val verdictSystem = mock<VerdictIncidentsSystem>()
        val randomEvent = mock<RandomEventManager>()

        state.world.locationId = "wybrzeze_polnocne"

        val vm = CityViewModel(
            gameRepository,
            questEngine,
            cityCatalogue,
            npcGenerator,
            socialSystem,
            dialogueManager,
            atmosphericSystem,
            verdictSystem,
            randomEvent
        )

        vm.onEvent(CityUiEvent.ToggleQuestMenu(true))
        vm.onEvent(CityUiEvent.OnMarketClick)
        vm.onEvent(CityUiEvent.OnAlchemyClick)
        vm.onEvent(CityUiEvent.OnTavernClick)
        vm.onEvent(CityUiEvent.OnTempleClick)
        vm.onEvent(CityUiEvent.OnRecruitClick)
        vm.onEvent(CityUiEvent.OnExitClick)

        assertNotNull(vm.uiState.value)
    }

    @Test
    fun testHubViewModelRestAndEvents() {
        val visualSystem = mock<com.grimreich.systems.VisualContentSystem>()
        val atmosphericLog = mock<com.grimreich.systems.AtmosphericLogSystem>()
        val randomEvent = mock<RandomEventManager>()
        val endingSystem = mock<EndingSystem>()

        state.gold = 100
        state.party.add(Hero(id = "h1", name = "Hero1", hp = 10, maxHp = 40, sanity = 50))

        val vm = HubViewModel(
            gameRepository,
            questEngine,
            cityCatalogue,
            visualSystem,
            atmosphericLog,
            randomEvent,
            endingSystem
        )

        vm.restAtCampfire()
        assertEquals(80, state.gold)
        assertEquals(20, state.party[0].hp) // 10 + 10 heal
        assertEquals(65, state.party[0].sanity) // 50 + 15

        vm.triggerHubEvent()
        vm.checkForEnding {}
    }

    @Test
    fun testChronicleViewModelTabs() {
        val chronicleSystem = mock<ChronicleSystem>()
        whenever(chronicleSystem.getUnlockedEntries()).thenReturn(emptyList())

        val vm = ChronicleViewModel(chronicleSystem, gameRepository)
        assertEquals(ChronicleTab.CHRONICLE_LORE, vm.selectedTab.value)

        vm.selectTab(ChronicleTab.PRZEWODNIK_WIKI)
        assertEquals(ChronicleTab.PRZEWODNIK_WIKI, vm.selectedTab.value)
    }
}
