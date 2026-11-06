package com.grimreich.systems

import android.content.Context
import com.grimreich.core.GameRepository
import com.grimreich.core.SaveSystem
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DestructionWipeIntegrationTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var context: Context
    private lateinit var persistence: StatePersistenceManager
    private lateinit var saveSystem: SaveSystem
    private lateinit var cityCatalogue: CityCatalogue
    private lateinit var itemCatalogue: ItemCatalogue
    private lateinit var repo: GameRepository

    @Before
    fun setup() {
        context = mock()
        whenever(context.filesDir).thenReturn(tempFolder.root)

        persistence = StatePersistenceManager(context)
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
    fun `test destruction wipe completely purges real disk files and blocks subsequent saves`() = runTest {
        // 1. Simulate gameplay and initial save
        repo.updateState { it.gold = 500 }
        repo.flushAndPersistSync()

        assertTrue("Session file should exist on disk after save", persistence.hasPersistedSession())

        // 2. Launch concurrent background save requests
        val job = launch(Dispatchers.IO) {
            delay(5)
            repo.persistCurrentState()
        }

        // 3. Trigger Wipe (Destrukcja)
        repo.deleteAllDataAndWipe()
        assertTrue("Repository should be flagged as wiped", repo.isWiped)

        // 4. Try to save again from UI thread (e.g. setMode(main_menu) fallback)
        repo.persistCurrentState()
        repo.flushAndPersistSync()

        job.join()
        delay(100)

        // 5. Verify real disk files are completely GONE
        assertFalse("Session file MUST NOT exist on disk after Destruction Wipe!", persistence.hasPersistedSession())
        assertFalse("Save slots file MUST NOT exist on disk after Destruction Wipe!", persistence.hasPersistedSlots())
    }
}