package com.darklandsmobile.ui.expedition

import com.darklandsmobile.core.GrimSeed
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class OtherSideViewModelTest {
    @Before
    fun setup() {
        GrimSeed.initialize()
    }

    @Test
    fun start_for_current_region_populates_state() {
        val vm = OtherSideViewModel()
        vm.startForCurrentRegion()
        val s = vm.state.value
        assertNotNull(s)
        assertTrue(!s!!.enemies.isNullOrEmpty())
        assertTrue(!s.rewards.isNullOrEmpty())
    }
}
