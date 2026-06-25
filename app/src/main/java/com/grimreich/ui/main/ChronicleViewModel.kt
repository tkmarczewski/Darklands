package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.ChronicleEntry
import com.grimreich.systems.ChronicleSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ChronicleViewModel @Inject constructor(
    private val chronicleSystem: ChronicleSystem,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _unlockedEntries = MutableStateFlow<List<ChronicleEntry>>(emptyList())
    val unlockedEntries: StateFlow<List<ChronicleEntry>> = _unlockedEntries.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                _unlockedEntries.value = chronicleSystem.getUnlockedEntries()
            }
            .launchIn(viewModelScope)
    }
}
