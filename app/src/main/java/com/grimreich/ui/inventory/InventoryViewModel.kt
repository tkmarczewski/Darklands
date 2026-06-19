package com.grimreich.ui.inventory

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.grimreich.v1.Item
import com.grimreich.systems.InventorySystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    init {
        refresh()
    }

    fun selectHero(heroId: String) {
        val hero = gameRepository.currentState().party.find { it.id == heroId }
        _uiState.update { it.copy(activeHero = hero) }
    }

    fun selectItem(item: Item?) {
        _uiState.update { it.copy(selectedItem = item) }
    }

    fun equipItem() {
        val hero = _uiState.value.activeHero ?: return
        val item = _uiState.value.selectedItem ?: return
        inventorySystem.equip(hero.id, item.id)
        refresh()
    }

    fun unequipItem(slot: String) {
        val hero = _uiState.value.activeHero ?: return
        inventorySystem.unequip(hero.id, slot)
        refresh()
    }

    fun refresh() {
        val state = gameRepository.currentState()
        _uiState.update { 
            it.copy(
                inventory = state.inventory.toList(),
                activeHero = if (it.activeHero != null) state.party.find { h -> h.id == it.activeHero.id } else null
            )
        }
    }
}
