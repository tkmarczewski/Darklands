package com.grimreich.ui

import android.content.Context
import com.grimreich.systems.GameViewModel
import com.grimreich.core.GameRepository

/**
 * Lightweight adapter exposing fully rendered screen text for early UI wiring,
 * previews, or temporary non-Compose integration.
 */
class GameplayUiController(
    private val viewModel: GameViewModel = GameViewModel()
) {
    fun city(): String = GameplayScreens.renderCity(viewModel.cityScreenState)

    fun journal(): String = GameplayScreens.renderJournal(viewModel.journalState)

    fun acceptQuest(questId: String): String {
        viewModel.acceptQuest(questId)
        return city()
    }

    fun travel(): String {
        viewModel.travelToActiveQuest()
        return GameplayScreens.renderTravel(requireNotNull(viewModel.travelScreenState))
    }

    fun resolve(context: Context): String {
        viewModel.resolveActiveQuest(context)
        return GameplayScreens.renderResolution(requireNotNull(viewModel.resolutionScreenState))
    }

    fun save(context: Context): String {
        viewModel.saveGame(context)
        return "Saved game state to persistent storage."
    }

    fun load(context: Context): String {
        val success = viewModel.loadGame(context)
        return if (success) {
            "Loaded game from persistent storage. City: ${GameRepository.state.grimCurrentRegion}"
        } else {
            "No save found or load failed"
        }
    }
}
