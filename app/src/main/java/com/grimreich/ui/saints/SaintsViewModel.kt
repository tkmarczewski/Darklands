package com.grimreich.ui.saints

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.core.SaintCatalogue
import com.grimreich.systems.ChurchSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SaintsUiState(
    val partyStatus: String = "",
    val saintsText: String = "",
    val log: String = "Stoisz przed ołtarzem Absolutu..."
)

class SaintsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SaintsUiState())
    val uiState: StateFlow<SaintsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun pray() {
        val state = GameRepository.state
        val hero = state.party.find { it.id == state.activeHeroId } ?: state.party.firstOrNull() ?: return
        val msg = ChurchSystem.pray(hero)
        updateLog(msg)
        refresh()
    }

    fun cleanse() {
        val state = GameRepository.state
        val hero = state.party.find { it.id == state.activeHeroId } ?: state.party.firstOrNull() ?: return
        val msg = ChurchSystem.cleanseRelic(hero)
        updateLog(msg)
        refresh()
    }

    private fun updateLog(text: String) {
        _uiState.update { it.copy(log = text) }
    }

    fun refresh() {
        val g = GameRepository.state
        val partyStatus = g.party.joinToString("\n") { h ->
            "${h.name}: Favor=${h.divineFavor}, Virtue=${h.virtue}, Corruption=${h.corruption}, Sanity=${h.sanity}%"
        }
        
        val saintsList = SaintCatalogue.all()
        val saintsText = if (saintsList.isEmpty()) "Brak świętych." else saintsList.joinToString("\n\n") { saint ->
            "${saint.name}\n  domain: ${saint.domain}\n  patronage: ${saint.patronage}"
        }

        _uiState.update { 
            it.copy(
                partyStatus = if (partyStatus.isBlank()) "Brak bohaterów." else partyStatus,
                saintsText = saintsText
            )
        }
    }
}
