package com.grimreich.systems

import android.content.Context
import android.content.res.AssetManager
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.WorldMap
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.*
import java.io.ByteArrayInputStream

class ContentValidationTest {

    private fun createMockContext(): Context {
        val context = mock(Context::class.java)
        val assets = mock(AssetManager::class.java)
        `when`(context.assets).thenReturn(assets)
        
        // Mock a basic dialogue JSON for unit tests
        val dummyJson = """
        [
          {
            "id": "guard_start",
            "npcId": "guard",
            "text": "Stój! Mgła gęstnieje.",
            "choices": []
          },
          {
            "id": "merchant_start",
            "npcId": "merchant",
            "text": "Witaj, podróżniku.",
            "choices": []
          },
          {
            "id": "aelion_start",
            "npcId": "aelion",
            "text": "Kotwico...",
            "choices": []
          },
          {
            "id": "mira_start",
            "npcId": "mira",
            "text": "Lustra nie kłamią.",
            "choices": []
          }
        ]
        """.trimIndent()
        
        `when`(assets.open("grimreich/dialogues_pilot.json")).thenReturn(ByteArrayInputStream(dummyJson.toByteArray()))
        return context
    }

    @Test
    fun dialogueNodes_shouldHaveValidNpcIds() {
        val mockRepository = mock(GameRepository::class.java)
        `when`(mockRepository.currentState()).thenReturn(GameState())

        val manager = DialogueManager(
            context = createMockContext(),
            gameRepositoryProvider = dagger.Lazy { mockRepository },
            questEngine = dagger.Lazy { mock(QuestEngine::class.java) }
        )

        manager.seedBasicDialogues()

        val guardNode = manager.getNode("guard_start")
        assertTrue("Node should exist", guardNode != null)
        assertEquals("guard", guardNode?.npcId)
        assertEquals("Stój! Mgła gęstnieje.", guardNode?.text)

        assertTrue("DialogueManager should have seeded nodes", manager.hasNode("guard_start"))
        assertTrue("DialogueManager should have seeded nodes", manager.hasNode("merchant_start"))
        assertTrue("DialogueManager should have seeded nodes", manager.hasNode("aelion_start"))
        assertTrue("DialogueManager should have seeded nodes", manager.hasNode("mira_start"))
    }

    @Test
    fun worldMap_shouldReferenceExistingCities() {
        val worldMap = WorldMap()
        worldMap.seedStage1(1)
        
        val cityCatalogue = CityCatalogue()
        cityCatalogue.seedCanonical()

        val issues = worldMap.validateCityReferences(cityCatalogue.allIds())
        assertTrue("World map city reference issues: $issues", issues.isEmpty())
    }

    @Test
    fun glitchText_shouldBeDeterministic() {
        val mockRepository = mock(GameRepository::class.java)
        `when`(mockRepository.currentState()).thenReturn(GameState())

        val manager = DialogueManager(
            context = createMockContext(),
            gameRepositoryProvider = dagger.Lazy { mockRepository },
            questEngine = dagger.Lazy { mock(QuestEngine::class.java) }
        )

        val lowStabilityState = GameState(world = com.grimreich.core.WorldState(globalStability = 20))
        `when`(mockRepository.currentState()).thenReturn(lowStabilityState)

        manager.seedBasicDialogues()

        val node1 = manager.getNode("guard_start")
        val node2 = manager.getNode("guard_start")

        assertEquals(node1?.text, node2?.text)
    }

    @Test
    fun getNode_shouldReturnNullForNonExistentId() {
        val manager = DialogueManager(
            context = createMockContext(),
            gameRepositoryProvider = dagger.Lazy { mock(GameRepository::class.java) },
            questEngine = dagger.Lazy { mock(QuestEngine::class.java) }
        )
        manager.seedBasicDialogues()
        
        assertEquals(null, manager.getNode("non_existent"))
    }

    @Test
    fun glitchText_shouldHandleEmptyString() {
        val manager = DialogueManager(
            context = mock(Context::class.java),
            gameRepositoryProvider = dagger.Lazy { mock(GameRepository::class.java) },
            questEngine = dagger.Lazy { mock(QuestEngine::class.java) }
        )
        
        assertEquals("", manager.glitchText("", 123L))
    }

    @Test
    fun getPortrait_shouldReturnDefaultForUnknownRole() {
        val manager = DialogueManager(
            context = mock(Context::class.java),
            gameRepositoryProvider = dagger.Lazy { mock(GameRepository::class.java) },
            questEngine = dagger.Lazy { mock(QuestEngine::class.java) }
        )
        
        assertEquals("port_peasant", manager.getPortrait("UNKNOWN"))
    }
}
