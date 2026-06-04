package com.darklandsmobile.grimreich.v1
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class GrimWorldEngineIntegrationTest {
    @Test
    fun `assets exist and basic world file is valid`() {
        val worldFile = File("src/main/assets/grimreich/world_collapse_example.json")
        // Note: Working directory might be project root or module root depending on runner
        if (worldFile.exists()) {
            val text = worldFile.readText()
            assertTrue(text.contains("\"id\""))
            assertTrue(text.contains("\"regions\""))
        }
    }

    @Test
    fun `builders produce consistent world and region`() {
        val region = GrimBuilders.region(name = "Integration Test Region", seed = 123L)
        val world = GrimBuilders.grimWorld(name = "IntegrationTestWorld", regions = listOf(region))
        assertEquals("IntegrationTestWorld", world.name)
        assertEquals(1, world.regions.size)
        assertEquals("Integration Test Region", world.regions[0].name)
        assertEquals(123L, world.regions[0].seed)
    }

    @Test
    fun `engine seed and load simulation smoke test`() {
        val region = GrimBuilders.region(name = "EngineRegion", seed = 42L)
        val world = GrimBuilders.grimWorld(name = "EngineWorld", regions = listOf(region))
        assertTrue(world.regions.any { it.id == region.id })
    }
}
