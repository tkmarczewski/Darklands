package com.grimreich.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

/**
 * Utility for ensuring game save integrity via checksums.
 * Part of the Post-Audit Security Hardening.
 */
object SaveIntegrity {
    
    /**
     * UWAGA: SALT jest obecnie zakodowany na sztywno, co chroni przed przypadkowym
     * uszkodzeniem pliku, ale nie przed celową manipulacją (security theater).
     * W wersji produkcyjnej zaleca się użycie soli unikalnej dla urządzenia.
     */
    private const val SALT = "GRIM_CIPHER_2026"

    /**
     * Generates a SHA-256 checksum for the given JSON string on a background thread.
     */
    suspend fun generateChecksum(json: String): String = withContext(Dispatchers.Default) {
        val bytes = (json + SALT).toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        digest.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies if the provided checksum matches the JSON string on a background thread.
     */
    suspend fun verify(json: String, checksum: String): Boolean {
        return generateChecksum(json) == checksum
    }

    /**
     * Computes a quick hash of the game state for dirty-checking and change detection.
     * BUG-16: Now includes hero progress (HP+XP).
     */
    fun computeStateHash(state: GameState): Int {
        var result = state.world.day
        result = 31 * result + state.gold
        result = 31 * result + state.party.sumOf { it.hp + it.xp + it.level }
        result = 31 * result + state.quest.completedQuestIds.size
        result = 31 * result + state.quest.activeQuestIds.size
        result = 31 * result + state.quest.failedQuestIds.size
        result = 31 * result + state.reputation.globalFactions.values.sum()
        result = 31 * result + state.inventory.size
        result = 31 * result + (state.playerName?.hashCode() ?: 0)
        result = 31 * result + (state.heroName?.hashCode() ?: 0)
        return result
    }
}
