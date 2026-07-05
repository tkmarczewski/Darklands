package com.grimreich.core

import android.content.Context
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.QuestManifest
import com.grimreich.systems.StatePersistenceManager
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import org.junit.Test
import org.mockito.Mockito.mock

class DependencyInjectionFixTest {

    @Test
    fun gameRepository_shouldInitializeWithoutStackOverflow() {
        // Setup mocks
        val mockContext = mock(Context::class.java)
        val mockPersistence = mock(StatePersistenceManager::class.java)
        val cityCatalogue = CityCatalogue()
        val itemCatalogue = ItemCatalogue()

        // Manual dependency resolution (simulating Hilt)
        var repo: GameRepository? = null
        
        val questEngine = QuestEngine(
            gameRepositoryProvider = { repo!! } // Lazy resolution
        )
        
        val questManifest = QuestManifest(mockContext, questEngine)
        
        val dialogueManager = DialogueManager(
            context = mockContext,
            gameRepositoryProvider = { repo!! },
            chronicleSystem = dagger.Lazy { mock(com.grimreich.systems.ChronicleSystem::class.java) },
            questEngine = dagger.Lazy { questEngine }
        )

        // This is the call that used to fail with StackOverflowError
        repo = GameRepository(
            questEngineProvider = { questEngine },
            dialogueManagerProvider = { dialogueManager },
            questManifestProvider = { questManifest },
            persistence = mockPersistence,
            cityCatalogue = cityCatalogue,
            itemCatalogue = itemCatalogue
        )

        // Verify we can access the state
        assert(repo.currentState() != null)
        
        // Explicitly trigger sync to see if it loops now
        // It shouldn't, because questEngine uses repo!! which is now set
        repo.sync()
    }
}
