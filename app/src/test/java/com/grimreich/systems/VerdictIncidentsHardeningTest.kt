package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VerdictIncidentsHardeningTest {
    private lateinit var system: VerdictIncidentsSystem
    private lateinit var gameRepository: GameRepository
    private lateinit var questEngine: QuestEngine
    private val state = GameState()

    @Before
    fun setup() {
        gameRepository = mock()
        questEngine = mock()
        system = VerdictIncidentsSystem(gameRepository, questEngine)
        
        whenever(gameRepository.currentState()).thenReturn(state)
        // Simulate atomic updateState
        whenever(gameRepository.updateState(any<Boolean>(), any())).thenAnswer { invocation ->
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(state)
            null
        }
    }

    @Test
    fun `onCityEntered should increment incident count atomically`() {
        system.onCityEntered("city_1")
        
        val progress = state.quest.progress["meta_verdict_incidents"]
        assertEquals("1", progress?.variables?.get("count"))
        
        system.onCityEntered("city_2")
        val updatedProgress = state.quest.progress["meta_verdict_incidents"]
        assertEquals("2", updatedProgress?.variables?.get("count"))
    }

    @Test
    fun `onCityEntered should choose a path if none exists`() {
        system.onCityEntered("city_1")
        
        val pathChosen = state.quest.worldFlags.any { it.startsWith("verdict_path_") }
        assertTrue(pathChosen, "A path should be chosen on first city entry")
    }

    @Test
    fun `onCityEntered should track city history`() {
        system.onCityEntered("wybrzeze_polnocne")
        system.onCityEntered("wybrzeze_polnocne")
        
        val history = state.quest.progress["meta_verdict_history"]
        assertEquals("2", history?.variables?.get("wybrzeze_polnocne"))
    }
}
