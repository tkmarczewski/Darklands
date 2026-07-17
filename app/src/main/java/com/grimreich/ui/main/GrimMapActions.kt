package com.grimreich.ui.main

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrimMapActions @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun openRegion(root: GameRootViewModel, regionId: String) {
        gameRepository.updateState { state ->
            state.world.locationId = regionId
        }
        root.setMode(GameScreenMode.city)
    }

    fun openOtherSide(root: GameRootViewModel) {
        root.setMode(GameScreenMode.combat)
    }
}
