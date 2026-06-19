package com.grimreich.ui.tavern

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.DialogueManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import java.util.UUID

data class RecruitmentUiState(
    val availableHeroes: List<Hero> = emptyList(),
    val gold: Int = 0
)

@HiltViewModel
class RecruitmentViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val dialogueManager: DialogueManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecruitmentUiState())
    val uiState: StateFlow<RecruitmentUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val state = gameRepository.currentState()
        // Mock heroes for now
        val heroes = listOf(
            Hero(UUID.randomUUID().toString(), "Klaus", 30, 12, 10, 10, 10, 12, 8, 10, 44, 44, portraitRes = dialogueManager.getPortrait("KNIGHT")),
            Hero(UUID.randomUUID().toString(), "Helga", 28, 10, 12, 11, 13, 10, 11, 9, 40, 40, portraitRes = dialogueManager.getPortrait("SCHOLAR"))
        )
        _uiState.update { it.copy(availableHeroes = heroes, gold = state.gold) }
    }

    fun hireHero(hero: Hero) {
        val state = gameRepository.currentState()
        if (state.gold >= 50) {
            state.gold -= 50
            state.party.add(hero)
            gameRepository.persistCurrentState()
            refresh()
        }
    }
}
