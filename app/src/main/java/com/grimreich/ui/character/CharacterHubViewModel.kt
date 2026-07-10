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

enum class CharacterHubTab { OVERVIEW, EQUIPMENT, PARTY }

data class CharacterHubUiState(
    val selectedHeroId: String? = null,
    val selectedTab: CharacterHubTab = CharacterHubTab.OVERVIEW,
    val heroes: List<Hero> = emptyList(),
    val inventory: List<Item> = emptyList(),
    val isLoading: Boolean = false
) {
    val selectedHero: Hero? get() = heroes.find { it.id == selectedHeroId }
}

@HiltViewModel
class CharacterHubViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val inventorySystem: InventorySystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterHubUiState(isLoading = true))
    val uiState: StateFlow<CharacterHubUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { updateUiState(it) }
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

    private fun updateUiState(state: GameState) {
        _uiState.update { 
            it.copy(
                heroes = state.party,
                inventory = state.inventory,
                selectedHeroId = it.selectedHeroId ?: state.activeHeroId ?: state.party.firstOrNull()?.id,
                isLoading = false
            )
        }
    }

    private fun equipItem(instanceId: String) {
        val heroId = _uiState.value.selectedHeroId ?: return
        inventorySystem.equip(heroId, instanceId)
    }

    private fun unequipItem(slot: String) {
        val heroId = _uiState.value.selectedHeroId ?: return
        inventorySystem.unequip(heroId, slot)
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
