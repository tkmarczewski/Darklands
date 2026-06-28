package com.grimreich.systems

import android.content.Context
import android.util.Log
import com.grimreich.core.SaveSnapshot
import com.grimreich.core.SessionStateDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
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
    
    private val gson = Gson()

    private val sessionFileName = "current_session.json"
    private val sessionFile: File get() = File(context.filesDir, sessionFileName)
    
    private val slotsFileName = "save_slots.json"
    private val slotsFile: File get() = File(context.filesDir, slotsFileName)

    fun persist(session: SessionStateDto) {
        synchronized(this) {
            try {
                Log.d(TAG, "Persisting session to: ${sessionFile.absolutePath}")
                val jsonString = json.encodeToString(SessionStateDto.serializer(), session)
                FileOutputStream(sessionFile).use { fos ->
                    fos.write(jsonString.toByteArray())
                    fos.flush()
                    fos.fd.sync()
                }
                Log.d(TAG, "Session persisted successfully. Size: ${sessionFile.length()} bytes")
            } catch (e: Exception) {
                Log.e(TAG, "Blad zapisu sesji do pliku: $sessionFileName", e)
            }
        }
    }

    fun restore(): SessionStateDto? {
        if (!sessionFile.exists()) {
            Log.d(TAG, "Session file does not exist: ${sessionFile.absolutePath}")
            return null
        }
        return try {
            val content = sessionFile.readText()
            Log.d(TAG, "Restoring session. Content size: ${content.length}")
            json.decodeFromString(SessionStateDto.serializer(), content)
        } catch (e: Exception) {
            Log.e(TAG, "Blad wczytywania sesji z pliku: $sessionFileName", e)
            null
        }
    }

    fun exists(): Boolean = sessionFile.exists()

    fun clear() {
        Log.d(TAG, "Clearing persistence")
        if (sessionFile.exists()) sessionFile.delete()
        if (slotsFile.exists()) slotsFile.delete()
    }

    fun persistSlots(slots: Map<Int, SaveSnapshot>) {
        synchronized(this) {
            try {
                Log.d(TAG, "Persisting slots to: ${slotsFile.absolutePath}")
                val data = gson.toJson(slots)
                FileOutputStream(slotsFile).use { fos ->
                    fos.write(data.toByteArray())
                    fos.flush()
                    fos.fd.sync()
                }
                Log.d(TAG, "Slots persisted successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Blad zapisu slotow", e)
            }
        }
    }

    fun restoreSlots(): Map<Int, SaveSnapshot> {
        if (!slotsFile.exists()) return emptyMap()
        return try {
            val type = object : TypeToken<Map<Int, SaveSnapshot>>() {}.type
            gson.fromJson(slotsFile.readText(), type) ?: emptyMap()
        } catch (e: Exception) {
            Log.e(TAG, "Blad odczytu slotow", e)
            if (slotsFile.exists()) slotsFile.delete()
            emptyMap()
        }
    }

    fun hasPersistedSession(): Boolean = sessionFile.exists()
    fun hasPersistedSlots(): Boolean = slotsFile.exists()

    fun debugPaths(): Map<String, String> = mapOf(
        "session" to sessionFile.absolutePath,
        "slots" to slotsFile.absolutePath
    )
}
