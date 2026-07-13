package com.grimreich.systems

import android.content.Context
import android.util.Log
import com.grimreich.core.SaveIntegrity
import com.grimreich.core.SaveSnapshot
import com.grimreich.core.SaveSnapshotDto
import com.grimreich.core.SessionStateDto
import com.grimreich.core.toDomain
import com.grimreich.core.toDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatePersistenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "StatePersistenceManager"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    private val mutex = Mutex()

    private val sessionFileName = "current_session.json"
    private val sessionFile: File get() = File(context.filesDir, sessionFileName)

    private val slotsFileName = "save_slots.json"
    private val slotsFile: File get() = File(context.filesDir, slotsFileName)

    suspend fun persist(session: SessionStateDto) = withContext(Dispatchers.IO) {
        // Project Anchor: Ensure data is consistent before write
        val dataJson = json.encodeToString(SessionStateDto.serializer(), session)
        val checksum = SaveIntegrity.generateChecksum(dataJson)
        val finalContent = "$checksum\n$dataJson"

        mutex.withLock {
            try {
                Log.d(TAG, "Persisting session to: ${sessionFile.absolutePath}")
                FileOutputStream(sessionFile).use { fos ->
                    fos.write(finalContent.toByteArray())
                    fos.flush()
                    fos.fd.sync()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Blad zapisu sesji", e)
            }
        }
    }

    suspend fun restore(): SessionStateDto? = withContext(Dispatchers.IO) {
        if (!sessionFile.exists()) {
            Log.d(TAG, "Session file does not exist: ${sessionFile.absolutePath}")
            return@withContext null
        }
        val (checksum, dataJson) = mutex.withLock {
            try {
                val content = sessionFile.readText()
                val lines = content.split("\n", limit = 2)
                if (lines.size < 2) return@withLock null to null
                lines[0] to lines[1]
            } catch (e: Exception) {
                Log.e(TAG, "Blad wczytywania pliku sesji", e)
                null to null
            }
        }
        
        if (checksum == null || dataJson == null) return@withContext null
            
        // SECURITY: Verify integrity checksum
        if (!SaveIntegrity.verify(dataJson, checksum)) {
            Log.e(TAG, "Naruszona integralnosc sesji (Checksum mismatch)!")
            return@withContext null
        }
        
        return@withContext try {
            json.decodeFromString(SessionStateDto.serializer(), dataJson).copy(checksum = checksum)
        } catch (e: Exception) {
            Log.e(TAG, "Blad deserializacji sesji", e)
            null
        }
    }

    fun exists(): Boolean = sessionFile.exists()

    /**
     * Clears BOTH the session file and the slots file.
     * Use this for a full reset (e.g. clearSessionAndReset).
     * Do NOT use this when only an outdated/corrupt session needs removing -
     * use [clearSessionOnly] to preserve manual save slots.
     */
    fun clear() {
        Log.d(TAG, "Clearing persistence (session + slots)")
        if (sessionFile.exists()) sessionFile.delete()
        if (slotsFile.exists()) slotsFile.delete()
    }

    /**
     * FIX: Clears only the session file, leaving the save slots file intact.
     *
     * Previously [GameRepository.restoreIfAvailable] called [clear] whenever a
     * session was absent or had an outdated version. This wiped the save_slots.json
     * file even though the player may have valid manual saves there.
     *
     * Use this when you only want to discard a stale/incompatible auto-save session
     * without touching the player's manual save slots.
     */
    fun clearSessionOnly() {
        Log.d(TAG, "Clearing session file only (slots preserved)")
        if (sessionFile.exists()) sessionFile.delete()
    }

    suspend fun persistSlots(slots: Map<Int, SaveSnapshot>) = withContext(Dispatchers.IO) {
        val slotsDto = slots.mapValues { it.value.toDto() }
        val serializer = MapSerializer(Int.serializer(), SaveSnapshotDto.serializer())
        val data = json.encodeToString(serializer, slotsDto)
        
        mutex.withLock {
            try {
                Log.d(TAG, "Persisting slots to: ${slotsFile.absolutePath}")
                FileOutputStream(slotsFile).use { fos ->
                    fos.write(data.toByteArray())
                    fos.flush()
                    fos.fd.sync() // Ensure physical disk write
                }
                Log.d(TAG, "Slots persisted successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Blad zapisu slotow", e)
            }
        }
    }

    suspend fun restoreSlots(): Map<Int, SaveSnapshot> = withContext(Dispatchers.IO) {
        if (!slotsFile.exists()) return@withContext emptyMap()
        
        mutex.withLock {
            try {
                val content = slotsFile.readText()
                val serializer = MapSerializer(Int.serializer(), SaveSnapshotDto.serializer())
                val slotsDto = json.decodeFromString(serializer, content)
                slotsDto.mapValues { it.value.toDomain() }
            } catch (e: Exception) {
                Log.e(TAG, "Blad odczytu slotow", e)
                if (slotsFile.exists()) slotsFile.delete()
                emptyMap()
            }
        }
    }

    fun hasPersistedSession(): Boolean = sessionFile.exists()
    fun hasPersistedSlots(): Boolean = slotsFile.exists()

    fun assets(): android.content.res.AssetManager = context.assets

    fun debugPaths(): Map<String, String> = mapOf(
        "session" to sessionFile.absolutePath,
        "slots" to slotsFile.absolutePath
    )
}
