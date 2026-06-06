package com.grimreich

import com.grimreich.ui.DemoFlowController
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DemoFlowControllerTest {

    private lateinit var demo: DemoFlowController

    @Before
    fun setup() {
        demo = DemoFlowController()
    }

    @Test
    fun `selecting city updates current state`() {
        val selected = demo.select("serce_krainy")
        assertTrue(selected.contains("Current city: serce_krainy"))
    }

    @Test
    fun `opening slice returns formatted text`() {
        val slice = demo.openCurrentSlice()
        assertTrue(slice.contains("VERTICAL SLICE"))
    }

    @Test
    fun `adding playtest note returns confirmation`() {
        val result = demo.addPlaytestNote("System test note.")
        assertTrue(result.contains("Note added"))
    }
}
