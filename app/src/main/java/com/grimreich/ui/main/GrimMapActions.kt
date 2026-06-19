package com.grimreich.ui.main

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrimMapActions @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun openRegion(root: GameRootViewModel, regionId: String) {
        val state = gameRepository.currentState()
        state.grimCurrentRegion = regionId
        gameRepository.persistCurrentState()
        root.setMode(GameScreenMode.CITY)
    }

    fun openOtherSide(root: GameRootViewModel) {
        root.setMode(GameScreenMode.COMBAT)
    }
}
