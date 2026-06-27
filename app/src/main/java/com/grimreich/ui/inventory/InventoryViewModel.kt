package com.grimreich.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.grimreich.v1.Item
import com.grimreich.systems.InventorySystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class InventoryUiState(
    val activeHero: Hero? = null,
    val inventory: List<Item> = emptyList(),
    val selectedItem: Item? = null
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val inventorySystem: InventorySystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private var selectedHeroId: String? = null

    init {
        gameRepository.gameState
            .onEach { refresh() }
            .launchIn(viewModelScope)
    }

    fun selectHero(heroId: String) {
        selectedHeroId = heroId
        refresh()
    }

    fun selectItem(item: Item?) {
        _uiState.update { it.copy(selectedItem = item) }
    }

    fun equipItem() {
        val heroId = selectedHeroId ?: gameRepository.currentState().activeHeroId ?: return
        val item = _uiState.value.selectedItem ?: return
        inventorySystem.equip(heroId, item.id)
    }

    fun unequipItem(slot: String) {
        val heroId = selectedHeroId ?: gameRepository.currentState().activeHeroId ?: return
        inventorySystem.unequip(heroId, slot)
    }

    fun refresh() {
        val state = gameRepository.currentState()
        val heroId = selectedHeroId ?: state.activeHeroId
        val hero = state.party.find { it.id == heroId }
        
        _uiState.update { 
            it.copy(
                activeHero = hero,
                inventory = state.inventory
            )
        }
    }
}
