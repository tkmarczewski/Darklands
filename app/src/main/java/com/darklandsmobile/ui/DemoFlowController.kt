package com.darklandsmobile.ui

import com.darklandsmobile.core.DemoShellState
import com.darklandsmobile.systems.DemoShellSystem
import com.darklandsmobile.systems.GameplayUiController
import com.darklandsmobile.systems.MagdeburgSliceSystem
import com.darklandsmobile.systems.RegionalSliceSystem

class DemoFlowController {
    private val gameplay = GameplayUiController()
    var shellState: DemoShellState = DemoShellSystem.build()
        private set

    init {
        MagdeburgSliceSystem.seed()
        RegionalSliceSystem.seedAll()
    }

    fun mainMenu(): String = DemoShellScreen.render(shellState)

    fun select(cityId: String): String {
        shellState = DemoShellSystem.selectCity(shellState, cityId)
        return DemoShellScreen.render(shellState)
    }

    fun openCurrentSlice(): String {
        val cityId = shellState.currentCityId ?: return "No city selected"
        return if (cityId == "magdeburg") {
            val view = MagdeburgSliceSystem.view(gameplayViewPlayerState())
            MagdeburgSliceScreen.render(view)
        } else {
            val view = RegionalSliceSystem.view(cityId, gameplayViewPlayerState(cityId))
            RegionalSliceScreen.render(view)
        }
    }

    fun addPlaytestNote(note: String): String {
        val cityId = shellState.currentCityId ?: return "No city selected"
        shellState = DemoShellSystem.addNote(shellState, cityId, note)
        return DemoShellScreen.render(shellState)
    }

    private fun gameplayViewPlayerState(cityId: String = "magdeburg") =
        com.darklandsmobile.core.PlayerState(currentCityId = cityId)
}
