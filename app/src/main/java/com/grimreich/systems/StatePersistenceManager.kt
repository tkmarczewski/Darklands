package com.grimreich.systems

import android.content.Context
import android.util.Log
import com.grimreich.core.SessionStateDto
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

    private val sessionFileName = "current_session.json"
    private val sessionFile: File get() = File(context.filesDir, sessionFileName)

    fun persist(session: SessionStateDto) {
        try {
            sessionFile.writeText(json.encodeToString(SessionStateDto.serializer(), session))
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
        if (sessionFile.exists()) {
            sessionFile.delete()
        }
    }
}
