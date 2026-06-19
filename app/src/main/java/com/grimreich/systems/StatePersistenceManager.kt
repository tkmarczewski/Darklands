package com.grimreich.systems

import android.content.Context
import com.grimreich.core.SessionStateDto
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatePersistenceManager @Inject constructor(
    private val context: Context
) {
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
            e.printStackTrace()
        }
    }

    fun restore(): SessionStateDto? {
        if (!sessionFile.exists()) return null
        return try {
            json.decodeFromString(SessionStateDto.serializer(), sessionFile.readText())
        } catch (e: Exception) {
            e.printStackTrace()
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
