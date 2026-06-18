package com.grimreich.ui.main

import android.content.Context
import android.content.Intent
import com.grimreich.core.GameRepository

/**
 * DEPRECATED Helper: Map actions now handled via GameRootViewModel.setMode().
 * Legacy startActivity calls replaced with state-driven navigation.
 */
object GrimMapActions {
    
    fun openRegion(root: GameRootViewModel, regionId: String) {
        GameRepository.state.grimCurrentRegion = regionId
        root.setMode(GameScreenMode.CITY)
    }

    fun openOtherSide(root: GameRootViewModel) {
        // Logic for Other Side phenomenon
        root.setMode(GameScreenMode.COMBAT)
    }
}
