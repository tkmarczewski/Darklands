package com.grimreich.systems

import com.grimreich.core.GameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WikiCodexTest {

    @Test
    fun testAnchorIdentityProgressiveReveal() {
        val state = GameState()
        state.metaAwarenessLevel = 0

        // 1. Initial State: Player should NOT know they are the Anchor
        val initialWikiList = WikiCodex.getEntriesForState(state)
        val anchorEntryInitial = initialWikiList.find { it.id == "wiki_anchor" }
        
        assertNotNull(anchorEntryInitial)
        assertFalse("Anchor identity must be locked at start", anchorEntryInitial!!.isUnlocked)
        assertEquals("Tożsamość Wędrowca (Bohater)", anchorEntryInitial.title)
        assertTrue("Locked text must refer to BG/Wędrowiec", anchorEntryInitial.fullText.contains("wędrowiec"))
        assertFalse("Locked text must NOT reveal Anchor true nature", anchorEntryInitial.fullText.contains("KOTWICĄ całej symulacji"))

        // 2. Advanced State: Player reaches metaAwarenessLevel >= 3
        state.metaAwarenessLevel = 3
        val updatedWikiList = WikiCodex.getEntriesForState(state)
        val anchorEntryRevealed = updatedWikiList.find { it.id == "wiki_anchor" }

        assertNotNull(anchorEntryRevealed)
        assertTrue("Anchor identity must be revealed when metaAwareness >= 3", anchorEntryRevealed!!.isUnlocked)
        assertEquals("KOTWICA (Prawdziwe 'Ja')", anchorEntryRevealed.title)
        assertTrue("Revealed text must explain Anchor true role", anchorEntryRevealed.fullText.contains("KOTWICĄ całej symulacji"))
    }

    private fun assertNotNull(obj: Any?) {
        org.junit.Assert.assertNotNull(obj)
    }
}
