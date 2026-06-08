package com.grimreich

import com.grimreich.core.PlayerState
import com.grimreich.systems.GameLoopController
import com.grimreich.systems.RegionalSliceSystem
import com.grimreich.ui.GrimholdSliceScreen
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GrimholdSliceSystemTest {

    @Before
    fun reset() {
        GameLoopController.bootstrap()
    }

    @Test
    fun `grimhold slice provides artwork and featured contracts`() {
        val state = PlayerState(currentCityId = "grimhold")
        val viewData = RegionalSliceSystem.buildViewData(state)
        
        val rendered = GrimholdSliceScreen.render(viewData)
        
        assertTrue(rendered.contains("GRIMHOLD VERTICAL SLICE"))
        assertTrue(viewData.quests.isNotEmpty())
        assertTrue(viewData.backgroundUrl.contains("grimhold"))
    }
}
