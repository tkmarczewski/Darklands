package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.ui.main.EndingViewModel
import dagger.Lazy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AuditFixesVerificationTest {

    private lateinit var gameRepository: GameRepository
    private lateinit var state: GameState
    private lateinit var dialogueManager: DialogueManager

    @Suppress("UNCHECKED_CAST")
    @Before
    fun setup() {
        state = GameState()
        state.gold = 100
        gameRepository = mock()
        whenever(gameRepository.currentState()).thenAnswer { state }
        whenever(gameRepository.updateState(any<Boolean>(), any())).thenAnswer { invocation ->
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(state)
            null
        }

        val questEngine: QuestEngine = mock()
        dialogueManager = DialogueManager(
            mock(),
            Lazy { gameRepository },
            Lazy { questEngine }
        )
    }

    @Test
    fun `pay_gold should reject negative amounts and not increase gold`() {
        // Exploit attempt: pay_gold with -500
        dialogueManager.handleTrigger(state, "pay_gold", "-500")

        // Should NOT gain 500 gold; gold should remain 100
        assertEquals(100, state.gold)
    }

    @Test
    fun `pay_gold with valid positive amount should deduct gold correctly`() {
        dialogueManager.handleTrigger(state, "pay_gold", "40")
        assertEquals(60, state.gold)
    }

    @Test
    fun `give_gold with positive amount should grant gold`() {
        dialogueManager.handleTrigger(state, "give_gold", "250")
        assertEquals(350, state.gold)
    }

    @Test
    fun `EndingViewModel reboot should reset session and keep persistent meta`() {
        state.metaAwarenessLevel = 3
        whenever(gameRepository.clearSessionAndReset()).thenAnswer {
            val metaCopy = state.persistentMeta.copy(
                unlockedLegacyBuffs = state.persistentMeta.unlockedLegacyBuffs.toMutableSet(),
                unitedSelves = state.persistentMeta.unitedSelves.toMutableList()
            )
            state = GameState()
            state.persistentMeta = metaCopy
            null
        }

        val endingViewModel = EndingViewModel(gameRepository, mock())
        endingViewModel.reboot()

        assertTrue(state.persistentMeta.unlockedLegacyBuffs.contains("REINFORCED_ANCHOR"))
        assertEquals(1, state.persistentMeta.totalSessionsFinished)
    }

    @Test
    fun `EndingViewModel ascend should update persistent meta via updateState`() {
        state.metaAwarenessLevel = 4
        val endingViewModel = EndingViewModel(gameRepository, mock())
        endingViewModel.ascend()

        assertTrue(state.persistentMeta.unlockedLegacyBuffs.contains("SCRIBES_EYE"))
        assertEquals(1, state.persistentMeta.totalSessionsFinished)
    }
}
