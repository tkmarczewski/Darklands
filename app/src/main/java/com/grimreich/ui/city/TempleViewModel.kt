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
    val logs: List<String> = emptyList(),
    val isNegotiating: Boolean = false,
    val errorMessage: String? = null
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
                        party = state.party.map { h -> h.deepCopy() }, // BUG FIX #6: avoid mutable reference
                        faith = state.prayer.faith,
                        gold = state.gold
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun pray(heroId: String) {
        val result = churchSystem.pray(heroId)
        addLog(result)
    }

    private fun addLog(message: String) {
        _uiState.update { 
            val newLogs = (it.logs + message).takeLast(10)
            it.copy(logs = newLogs, errorMessage = null)
        }
    }

    fun makeOffering(amount: Int) {
        if (amount > _uiState.value.gold) {
            _uiState.update { it.copy(errorMessage = "Brak złota na ofiarę!") }
            return
        }
        val result = churchSystem.makeOffering(amount)
        addLog(result)
    }

    fun toggleNegotiation() {
        _uiState.update { it.copy(isNegotiating = !it.isNegotiating, errorMessage = null) }
    }

    fun resurrect(heroId: String) {
        val hero = _uiState.value.party.find { it.id == heroId } ?: return
        if (!hero.isDead) return

        val isNegotiated = _uiState.value.isNegotiating
        val cost = if (isNegotiated) 150 else 300
        
        if (_uiState.value.gold < cost) {
            _uiState.update { it.copy(errorMessage = "Brak złota na wskrzeszenie ($cost G)!") }
            return
        }

        val result = churchSystem.performResurrection(heroId, isNegotiated)
        addLog(result)
        _uiState.update { it.copy(isNegotiating = false) }
    }
}
