package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MainMenuUiState(
    val hasSession: Boolean = false
)

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainMenuUiState())
    val uiState: StateFlow<MainMenuUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.update { it.copy(hasSession = gameRepository.hasSession()) }
    }

    fun startNewGame() {
        gameRepository.clearSessionAndReset()
    }
}
