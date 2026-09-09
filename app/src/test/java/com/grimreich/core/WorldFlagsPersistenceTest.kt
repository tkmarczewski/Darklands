package com.grimreich.core

import org.junit.Test
import kotlin.test.assertTrue

class WorldFlagsPersistenceTest {

    @Test
    fun `worldFlags should survive DTO conversion`() {
        val state = GameState()
        state.quest.worldFlags.add("test_flag_1")
        state.quest.worldFlags.add("verdict_campaign_ready")

        // 1. To DTO
        val dto = state.toDto()
        assertTrue(dto.quest.worldFlags.contains("test_flag_1"))
        assertTrue(dto.quest.worldFlags.contains("verdict_campaign_ready"))

        // 2. To Domain
        val restored = dto.toDomain()
        assertTrue(restored.quest.worldFlags.contains("test_flag_1"))
        assertTrue(restored.quest.worldFlags.contains("verdict_campaign_ready"))
    }
}
