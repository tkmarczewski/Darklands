package com.grimreich.systems

import android.content.Context
import com.grimreich.core.CityScreenState
import com.grimreich.core.PlayerState
import com.grimreich.core.QuestJournalState
import com.grimreich.core.ResolutionScreenState
import com.grimreich.core.TravelScreenState

/**
 * Simple UI-facing state holder independent from Android framework classes.
 * Can later be wrapped by a real ViewModel.
 */
class GameViewModel {
    var playerState: PlayerState = GameLoopController.bootstrap()
        private set

    var cityScreenState: CityScreenState = GameLoopController.cityScreen(playerState)
        private set

    var travelScreenState: TravelScreenState? = null
        private set

    var resolutionScreenState: ResolutionScreenState? = null
        private set

    var journalState: QuestJournalState = QuestJournalSystem.build(playerState)
        private set

    fun refreshCityScreen() {
        cityScreenState = GameLoopController.cityScreen(playerState)
        journalState = QuestJournalSystem.build(playerState)
    }

    fun acceptQuest(questId: String) {
        playerState = GameLoopController.acceptQuest(playerState, questId)
        refreshCityScreen()
    }

    fun travelToActiveQuest() {
        val (updatedPlayer, travelState) = GameLoopController.travelToQuest(playerState)
        playerState = updatedPlayer
        travelScreenState = travelState
        refreshCityScreen()
    }

    fun resolveActiveQuest(context: Context) {
        val (updatedPlayer, resolutionState) = GameLoopController.resolveActiveQuest(playerState)
        playerState = updatedPlayer
        resolutionScreenState = resolutionState
        refreshCityScreen()
        SaveLoadSystem.save(context)
    }

    fun saveGame(context: Context) {
        SaveLoadSystem.save(context)
    }

    fun loadGame(context: Context): Boolean {
        val success = SaveLoadSystem.load(context)
        if (success) {
            // playerState is updated inside SaveLoadSystem.load for simplicity in this implementation
            // But we might need to sync it back if we want to keep GameViewModel in sync.
            // GameRepository.state is the source of truth now.
            refreshCityScreen()
        }
        return success
    }
}
