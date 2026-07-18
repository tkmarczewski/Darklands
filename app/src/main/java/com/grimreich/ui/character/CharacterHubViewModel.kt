package com.grimreich.ui.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.Hero
import com.grimreich.grimreich.v1.Item
import com.grimreich.systems.InventorySystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CharacterHubUiEvent {
    data class SelectHero(val heroId: String) : CharacterHubUiEvent
    data class SelectTab(val tab: CharacterHubTab) : CharacterHubUiEvent
    data class EquipItem(val instanceId: String) : CharacterHubUiEvent
    data class UnequipItem(val slot: String) : CharacterHubUiEvent
    data class ReorderParty(val fromIndex: Int, val toIndex: Int) : CharacterHubUiEvent
}

sealed interface CharacterHubUiEffect {
    data class ShowMessage(val message: String) : CharacterHubUiEffect
}

@HiltViewModel
class CharacterHubViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val inventorySystem: InventorySystem,
    private val mapper: CharacterHubUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterHubUiState(isLoading = true))
    val uiState: StateFlow<CharacterHubUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CharacterHubUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                val mapped = mapper.map(state)
                _uiState.update { 
                    mapped.copy(
                        selectedHeroId = it.selectedHeroId ?: mapped.selectedHeroId ?: mapped.heroes.firstOrNull()?.id,
                        selectedTab = it.selectedTab
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: CharacterHubUiEvent) {
        when (event) {
            is CharacterHubUiEvent.SelectHero -> {
                _uiState.update { it.copy(selectedHeroId = event.heroId) }
            }
            is CharacterHubUiEvent.SelectTab -> {
                _uiState.update { it.copy(selectedTab = event.tab) }
            }
            is CharacterHubUiEvent.EquipItem -> equipItem(event.instanceId)
            is CharacterHubUiEvent.UnequipItem -> unequipItem(event.slot)
            is CharacterHubUiEvent.ReorderParty -> reorderParty(event.fromIndex, event.toIndex)
        }
    }

    private fun equipItem(instanceId: String) {
        val heroId = _uiState.value.selectedHeroId ?: return
        val result = inventorySystem.equip(heroId, instanceId)
        viewModelScope.launch { _uiEffect.emit(CharacterHubUiEffect.ShowMessage(result)) }
    }

    private fun unequipItem(slot: String) {
        val heroId = _uiState.value.selectedHeroId ?: return
        val result = inventorySystem.unequip(heroId, slot)
        viewModelScope.launch { _uiEffect.emit(CharacterHubUiEffect.ShowMessage(result)) }
    }

    private fun reorderParty(from: Int, to: Int) {
        gameRepository.updateState { state ->
            if (from in state.party.indices && to in state.party.indices) {
                val hero = state.party.removeAt(from)
                state.party.add(to, hero)
            }
        }
    }
}
