package com.grimreich.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import com.grimreich.systems.StatePersistenceManager
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue

/**
 * NEW — ConcurrencyTest
 * Verifies GameRepository.updateState() is thread-safe under concurrent coroutine access.
 * Validates Mutex migration from synchronized(this).
 */
class ConcurrencyTest {

    private fun buildRepo(): GameRepository {
        val mockPersistence = mock(StatePersistenceManager::class.java)
        return GameRepository(
            questEngineProvider = dagger.Lazy { mock(com.grimreich.systems.QuestEngine::class.java) },
            dialogueManagerProvider = dagger.Lazy { mock(com.grimreich.systems.DialogueManager::class.java) },
            questManifestProvider = dagger.Lazy { mock(com.grimreich.systems.QuestManifest::class.java) },
            economySystemProvider = dagger.Lazy { mock(EconomyCalculator::class.java) },
            echoSystemProvider = dagger.Lazy { mock(EchoSystem::class.java) },
            persistence = mockPersistence,
            cityCatalogue = CityCatalogue(),
            itemCatalogue = ItemCatalogue(),
            saveSystem = mock(SaveSystem::class.java)
        )
    }

    @Test
    fun concurrentUpdateState_shouldNotCorruptGold() = runBlocking {
        val repo = buildRepo()

        val jobs = (1..100).map {
            launch(Dispatchers.Default) {
                repo.updateState("gold_increment") { state ->
                    state.gold += 1
                }
            }
        }
        jobs.joinAll()

        assertEquals(
            "100 concurrent gold increments should result in exactly 100 gold (no lost updates)",
            100,
            repo.currentState().gold
        )
    }

    @Test
    fun concurrentUpdateState_shouldNotCorruptParty() = runBlocking {
        val repo = buildRepo()
        val heroIds = (1..20).map { "hero_$it" }

        val addJobs = heroIds.map { id ->
            launch(Dispatchers.Default) {
                repo.updateState("add_hero_$id") { state ->
                    state.party.add(Hero(id = id, name = id, age = 25))
                }
            }
        }
        addJobs.joinAll()

        assertEquals(
            "All 20 heroes must be present after concurrent inserts",
            20,
            repo.currentState().party.size
        )
    }
}
