package com.grimreich.systems

import android.content.Context
import android.content.res.AssetManager
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.WorldMap
import com.grimreich.core.WorldState
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import java.io.ByteArrayInputStream
import java.io.IOException

class ContentValidationTest {

    private fun createMockContext(): Context {
        val context = mock(Context::class.java)
        val assets = mock(AssetManager::class.java)
        `when`(context.assets).thenReturn(assets)

        // AUDIT FIX: stub unknown paths to throw, known path returns data
        `when`(assets.open(any(String::class.java))).thenThrow(IOException("File not found"))

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
          },
          {
            "id": "innkeeper_start",
            "npcId": "innkeeper",
            "text": "Czego potrzebujesz?",
            "choices": [
              { "text": "Pokój", "nextId": "innkeeper_room" },
              { "text": "Odejdź", "nextId": null }
            ]
          }
        ]
        """.trimIndent()

        `when`(assets.open("grimreich/dialogues_pilot.json"))
            .thenReturn(ByteArrayInputStream(dummyJson.toByteArray()))
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

        assertTrue(manager.hasNode("guard_start"))
        assertTrue(manager.hasNode("merchant_start"))
        assertTrue(manager.hasNode("aelion_start"))
        assertTrue(manager.hasNode("mira_start"))
    }

    // AUDIT FIX: coverage for branching choices (previously missing)
    @Test
    fun dialogueNode_shouldHaveChoicesWhenDefined() {
        val mockRepository = mock(GameRepository::class.java)
        `when`(mockRepository.currentState()).thenReturn(GameState())

        val manager = DialogueManager(
            context = createMockContext(),
            gameRepositoryProvider = dagger.Lazy { mockRepository },
            questEngine = dagger.Lazy { mock(QuestEngine::class.java) }
        )
        manager.seedBasicDialogues()

        val innkeeperNode = manager.getNode("innkeeper_start")
        assertTrue("Innkeeper node should exist", innkeeperNode != null)
        assertEquals("innkeeper", innkeeperNode?.npcId)
        assertEquals("Node should have 2 choices", 2, innkeeperNode?.choices?.size)
        assertEquals("innkeeper_room", innkeeperNode?.choices?.first()?.nextId)
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

    // AUDIT FIX: determinism tested directly with explicit seed, not via cached node
    @Test
    fun glitchText_shouldBeDeterministicForSameSeed() {
        val manager = DialogueManager(
            context = mock(Context::class.java),
            gameRepositoryProvider = dagger.Lazy { mock(GameRepository::class.java) },
            questEngine = dagger.Lazy { mock(QuestEngine::class.java) }
        )
        val seed = 42L
        val input = "Stój! Mgła gęstnieje."
        val result1 = manager.glitchText(input, seed)
        val result2 = manager.glitchText(input, seed)
        assertEquals("glitchText must be deterministic for same seed", result1, result2)
    }

    // AUDIT FIX: verify glitch actually mutates text at low stability (was missing)
    @Test
    fun glitchText_shouldMutateTextWhenStabilityIsLow() {
        val mockRepository = mock(GameRepository::class.java)
        val lowStabilityState = GameState(world = WorldState(globalStability = 5))
        `when`(mockRepository.currentState()).thenReturn(lowStabilityState)

        val manager = DialogueManager(
            context = mock(Context::class.java),
            gameRepositoryProvider = dagger.Lazy { mockRepository },
            questEngine = dagger.Lazy { mock(QuestEngine::class.java) }
        )
        val original = "Stój! Mgła gęstnieje."
        val glitched = manager.glitchText(original, seed = 1337L)
        assertNotEquals("glitchText should mutate text at low stability", original, glitched)
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
