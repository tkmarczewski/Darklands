package com.grimreich.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

/**
 * Utility for ensuring game save integrity via checksums.
 * Part of the Post-Audit Security Hardening.
 */
object SaveIntegrity {
    
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
}
