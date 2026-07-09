package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.ChurchSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class TempleUiState(
    val party: List<Hero> = emptyList(),
    val faith: Int = 0,
    val gold: Int = 0,
    val logs: String = "",
    val isNegotiating: Boolean = false
)

@HiltViewModel
class TempleViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val churchSystem: ChurchSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(TempleUiState())
    val uiState: StateFlow<TempleUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                _uiState.update { 
                    it.copy(
                        party = state.party,
                        faith = state.prayer.faith,
                        gold = state.gold
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun pray(heroId: String) {
        val result = churchSystem.pray(heroId)
        _uiState.update { it.copy(logs = result) }
    }

    fun makeOffering(amount: Int) {
        val result = churchSystem.makeOffering(amount)
        _uiState.update { it.copy(logs = result) }
    }

    fun toggleNegotiation() {
        _uiState.update { it.copy(isNegotiating = !it.isNegotiating) }
    }

    fun resurrect(heroId: String) {
        val isNegotiated = _uiState.value.isNegotiating
        val result = churchSystem.performResurrection(heroId, isNegotiated)
        _uiState.update { it.copy(logs = result, isNegotiating = false) }
    }
}
