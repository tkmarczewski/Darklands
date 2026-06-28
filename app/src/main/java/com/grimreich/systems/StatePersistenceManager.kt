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
        try {
            val tmp = File(sessionFile.parentFile, sessionFile.name + ".tmp")
            tmp.writeText(json.encodeToString(SessionStateDto.serializer(), session))
            if (sessionFile.exists()) sessionFile.delete()
            if (!tmp.renameTo(sessionFile)) {
                Log.e(TAG, "Nie udalo sie przemianowac pliku sesji")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Blad zapisu sesji do pliku: $sessionFileName", e)
        }
    }

    fun restore(): SessionStateDto? {
        if (!sessionFile.exists()) return null
        return try {
            json.decodeFromString(SessionStateDto.serializer(), sessionFile.readText())
        } catch (e: Exception) {
            Log.e(TAG, "Blad wczytywania sesji z pliku: $sessionFileName. Gra zostanie zresetowana.", e)
            null
        }
    }

    fun exists(): Boolean = sessionFile.exists()

    fun clear() {
        if (sessionFile.exists()) sessionFile.delete()
        if (slotsFile.exists()) slotsFile.delete()
    }

    fun persistSlots(slots: Map<Int, SaveSnapshot>) {
        try {
            val data = gson.toJson(slots)
            val tmp = File(slotsFile.parentFile, slotsFile.name + ".tmp")
            tmp.writeText(data)
            if (slotsFile.exists()) slotsFile.delete()
            if (!tmp.renameTo(slotsFile)) {
                throw IllegalStateException("Nie udalo sie zapisac pliku slotow")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Blad zapisu slotow", e)
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
