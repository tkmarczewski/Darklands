package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MarketItem(
    val id: String,
    val name: String,
    val price: Int,
    val sellPrice: Int = (price * 0.6f).toInt()
)

data class MarketUiState(
    val cityName: String = "",
    val playerGold: Int = 0,
    val itemsForSale: List<MarketItem> = emptyList(),
    val itemsToSell: List<MarketItem> = emptyList(),
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
        cityCatalogue.seedCanonical()
        itemCatalogue.seed()
        refresh()
    }

    private fun toSlug(raw: String) = raw.lowercase()
        .replace("ą","a").replace("ć","c").replace("ę","e")
        .replace("ł","l").replace("ń","n").replace("ó","o")
        .replace("ś","s").replace("ź","z").replace("ż","z")
        .replace(" ","_")

    fun refresh() {
        val state = gameRepository.currentState()
        val cityId = toSlug(state.grimCurrentRegion)
        val cityData = cityCatalogue.get(cityId)
        val modifier = cityData?.priceModifier ?: 1.0f

        val forSale = itemCatalogue.all().shuffled(kotlin.random.Random(state.world.day + cityId.hashCode())).take(10).map { item ->
            MarketItem(
                id = item.id,
                name = item.name,
                price = (item.value * modifier).toInt().coerceAtLeast(1)
            )
        }

        val toSell = state.inventory.map { item ->
            val sellModifier = if (modifier > 1.0f) 0.5f else 0.6f // Anti-exploit: higher prices = worse sell ratio
            MarketItem(
                id = item.id,
                name = item.name,
                price = item.value,
                sellPrice = (item.value * sellModifier * modifier).toInt().coerceAtLeast(1)
            )
        }

        _uiState.update {
            it.copy(
                cityName = cityData?.name ?: "Nieznane Miasto",
                playerGold = state.gold,
                itemsForSale = forSale,
                itemsToSell = toSell,
                errorMessage = null
            )
        }
    }

    fun buy(itemId: String) {
        val state = gameRepository.currentState()
        val item = _uiState.value.itemsForSale.find { it.id == itemId } ?: return
        if (state.gold < item.price) {
            _uiState.update { it.copy(errorMessage = "Za mało złota.") }
            return
        }
        
        gameRepository.updateState { s ->
            s.gold -= item.price
            itemCatalogue.get(itemId)?.let { s.inventory.add(it.copy()) }
            s.logEntries.add("Zakupiono: ${item.name} za ${item.price} szt. złota.")
        }
        refresh()
    }

    fun sell(itemId: String) {
        val state = gameRepository.currentState()
        val index = state.inventory.indexOfFirst { it.id == itemId }
        if (index != -1) {
            val item = state.inventory[index]
            val cityId = toSlug(state.grimCurrentRegion)
            val cityData = cityCatalogue.get(cityId)
            val modifier = cityData?.priceModifier ?: 1.0f
            
            val sellModifier = if (modifier > 1.0f) 0.5f else 0.6f
            val sellPrice = (item.value * sellModifier * modifier).toInt().coerceAtLeast(1)
            
            gameRepository.updateState { s ->
                s.gold += sellPrice
                s.inventory.removeAt(index)
                s.logEntries.add("Sprzedano: ${item.name} za $sellPrice szt. złota.")
            }
            refresh()
        }
    }
}
