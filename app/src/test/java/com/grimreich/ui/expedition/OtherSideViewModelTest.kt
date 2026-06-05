package com.grimreich.ui.expedition

import com.grimreich.core.GameBootstrap
import com.grimreich.core.GameRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class OtherSideViewModelTest {

    @Before
    fun setup() {
        GameRepository.state = GameBootstrap.initialize()
    }

    @Test
    fun start_for_current_region_populates_state() {
        val vm = OtherSideViewModel()
        vm.startForCurrentRegion()
        val s = vm.state.value
        assertNotNull(s)
        assertTrue(s!!.enemies.isNotEmpty())
        assertTrue(s.rewards.isNotEmpty())
    }
}
