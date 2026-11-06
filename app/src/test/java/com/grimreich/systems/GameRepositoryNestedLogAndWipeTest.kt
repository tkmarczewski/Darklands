package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.SaveSystem
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GameRepositoryNestedLogAndWipeTest {

    private lateinit var persistence: StatePersistenceManager
    private lateinit var saveSystem: SaveSystem
    private lateinit var cityCatalogue: CityCatalogue
    private lateinit var itemCatalogue: ItemCatalogue
    private lateinit var repo: GameRepository

    @Before
    fun setup() {
        persistence = mock()
        saveSystem = mock()
        cityCatalogue = mock()
        itemCatalogue = mock()

        kotlinx.coroutines.runBlocking {
            whenever(saveSystem.wipeAll(any())).thenReturn(Unit)
        }

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
    fun `nested log inside updateState should modify current transaction copy and be preserved`() {
        repo.updateState { state ->
            state.gold = 500
            // Nested call to repo.log inside updateState
            repo.log("NESTED_LOG_ENTRY_123")
        }

        val logs = repo.currentState().logEntries
        assertTrue("Nested log message should be preserved in logEntries", logs.contains("NESTED_LOG_ENTRY_123"))
    }

    @Test
    fun `deleteAllDataAndWipe should completely reset state and wipe persistent meta`() = kotlinx.coroutines.test.runTest {
        repo.updateState { state ->
            state.gold = 9999
            state.persistentMeta.totalSessionsFinished = 10
            state.persistentMeta.unlockedLegacyBuffs.add("SCRIBES_EYE")
        }

        assertEquals(9999, repo.currentState().gold)
        assertEquals(10, repo.currentState().persistentMeta.totalSessionsFinished)

        repo.deleteAllDataAndWipe()

        assertTrue("repo.isWiped must be true after wipe", repo.isWiped)
        assertEquals("Gold should be reset to default initial gold (100)", 100, repo.currentState().gold)
        assertEquals("Total sessions finished should be reset to 0", 0, repo.currentState().persistentMeta.totalSessionsFinished)
        assertFalse("Unlocked legacy buffs should be empty after wipe", repo.currentState().persistentMeta.unlockedLegacyBuffs.contains("SCRIBES_EYE"))
    }

    @Test
    fun `persistCurrentState should do nothing when isWiped is true`() = kotlinx.coroutines.test.runTest {
        repo.deleteAllDataAndWipe()
        assertTrue(repo.isWiped)

        // Attempting to persist should be blocked
        repo.persistCurrentState()
        repo.flushAndPersistSync()

        // Verify persistence.persist was never called while isWiped was true
        org.mockito.kotlin.verify(persistence, org.mockito.kotlin.never()).persist(org.mockito.kotlin.any())
    }
}
