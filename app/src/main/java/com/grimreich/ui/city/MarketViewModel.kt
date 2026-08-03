package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

import com.grimreich.core.Hero

data class MarketItem(
    val id: String,
    val name: String,
    val price: Int,
    val sellPrice: Int
)

data class MarketUiState(
    val cityName: String = "",
    val playerGold: Int = 0,
    val itemsForSale: List<MarketItem> = emptyList(),
    val itemsToSell: List<MarketItem> = emptyList(),
    val party: List<Hero> = emptyList(), // DODANO dla dolnego paska V9
    val errorMessage: String? = null
)

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val cityCatalogue: CityCatalogue,
    private val itemCatalogue: ItemCatalogue
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { refresh() }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        val state = gameRepository.currentState()
        val cityId = state.world.locationId
        val city = cityCatalogue.get(cityId)
        
        val stock = city?.marketStock ?: emptyList()
        val forSale = stock.mapNotNull { itemId ->
            itemCatalogue.get(itemId)?.let { item ->
                MarketItem(item.templateId, item.name, item.value, calculateSellPrice(item.value))
            }
        }

        val toSell = state.inventory.map { item ->
            MarketItem(item.instanceId, item.name, item.value, calculateSellPrice(item.value))
        }

        _uiState.update { currentState -> 
            currentState.copy(
                cityName = city?.name ?: "Nieznane Miasto",
                playerGold = state.gold,
                itemsForSale = forSale,
                itemsToSell = toSell,
                party = state.party.map { hero -> hero.deepCopy() }, // Deep copy for UI stability
                errorMessage = null // Reset error on refresh
            )
        }
    }

    private fun calculateSellPrice(baseValue: Int): Int = (baseValue * 0.5).toInt()

    fun buy(itemId: String) {
        var purchaseSuccessful = false
        var itemName = ""
        var itemPrice = 0

        gameRepository.updateState { s ->
            // BUG FIX #3 & #4: Race condition - validate everything inside updateState
            val itemTemplate = itemCatalogue.get(itemId)
            if (itemTemplate == null || s.gold < itemTemplate.value) {
                return@updateState 
            }

            itemName = itemTemplate.name
            itemPrice = itemTemplate.value
            
            itemCatalogue.createInstance(itemId)?.let { instance ->
                s.gold -= itemPrice
                s.inventory.add(instance)
                s.logEntries.add("Kupiono: $itemName za $itemPrice G.")
                purchaseSuccessful = true
            }
        }
        
        // Final check for UI feedback
        if (!purchaseSuccessful) {
            _uiState.update { it.copy(errorMessage = "Nie można kupić przedmiotu (brak złota lub przedmiot niedostępny)!") }
        } else {
            _uiState.update { it.copy(errorMessage = null) }
        }
    }

    fun sell(itemId: String) {
        _uiState.update { it.copy(errorMessage = null) }
        
        gameRepository.updateState { s ->
            // BUG FIX #2: Find item inside updateState to avoid selling non-existent item
            val toRemove = s.inventory.find { it.instanceId == itemId }
            if (toRemove != null) {
                val price = calculateSellPrice(toRemove.value)
                s.inventory.remove(toRemove)
                s.gold += price
                s.logEntries.add("Sprzedano: ${toRemove.name} za $price G.")
            }
        }
    }
}
