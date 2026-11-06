package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.SaveSystem
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DestructionWipeIntegrationTest {

    private lateinit var persistence: StatePersistenceManager
    private lateinit var saveSystem: SaveSystem
    private lateinit var cityCatalogue: CityCatalogue
    private lateinit var itemCatalogue: ItemCatalogue
    private lateinit var repo: GameRepository

    private var mockPersistenceWiped = false
    private var mockPersistCalledAfterWipe = false

    @Before
    fun setup() {
        mockPersistenceWiped = false
        mockPersistCalledAfterWipe = false

        persistence = mock()
        runBlocking {
            whenever(persistence.clearAllDataAndSlots()).thenAnswer {
                mockPersistenceWiped = true
                Unit
            }
            whenever(persistence.persist(org.mockito.kotlin.any())).thenAnswer {
                if (repo.isWiped) {
                    mockPersistCalledAfterWipe = true
                }
                Unit
            }
        }

        saveSystem = SaveSystem()
        cityCatalogue = mock()
        itemCatalogue = mock()

        repo = GameRepository(
            questEngineProvider = dagger.Lazy { mock() },
            dialogueManagerProvider = dagger.Lazy { mock() },
            questManifestProvider = dagger.Lazy { mock() },
            economySystemProvider = dagger.Lazy { mock() },
            echoSystemProvider = dagger.Lazy { mock() },
            persistence = persistence,
            cityCatalogue = cityCatalogue,
            itemCatalogue = itemCatalogue,
            saveSystem = saveSystem
        )
    }

    @Test
    fun `test destruction wipe completely nullifies pending async saves and concurrent saves`() = runTest {
        // 1. Simulate gameplay
        repo.updateState { it.gold = 500 }

        // 2. Queue multiple rapid saves (simulating aggressive autosave / async lag)
        repo.persistCurrentState()
        repo.persistCurrentState()

        // 3. Launch a concurrent coroutine that tries to save while wiping
        val job = launch(Dispatchers.IO) {
            delay(10)
            repo.persistCurrentState()
        }

        // 4. Trigger Wipe (The "Destrukcja" action)
        repo.deleteAllDataAndWipe()
        assertTrue("Repository should be flagged as wiped", repo.isWiped)

        // 5. Try to save again from UI thread (e.g. setMode(main_menu) fallback)
        repo.persistCurrentState()
        repo.flushAndPersistSync()

        job.join()

        // Wait for repo coroutines to settle
        delay(200)

        assertTrue("Wipe mechanism was never invoked on persistence layer!", mockPersistenceWiped)
        assertFalse("Persist was allowed to execute after wipe flag was set! Race condition detected.", mockPersistCalledAfterWipe)
    }
}