package com.grimreich.systems

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestManifest @Inject constructor(
    @ApplicationContext private val context: Context,
    private val engine: QuestEngine
) {
    private val gson = Gson()

    fun seed() {
        engine.clearRegistry()
        loadQuestsFromAsset("grimreich/quests_pilot.json")
        loadQuestsFromAsset("grimreich/quests_extended.json")
    }

    private fun loadQuestsFromAsset(path: String) {
        try {
            val json = context.assets.open(path).bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<QuestDefinition>>() {}.type
            val loaded: List<QuestDefinition> = gson.fromJson(json, type)
            loaded.forEach { engine.register(it) }
            android.util.Log.d("QuestManifest", "✅ Loaded ${loaded.size} quests from $path")
        } catch (e: Exception) {
            android.util.Log.e("QuestManifest", "❌ Failed to load quests from $path: ${e.message}", e)
        }
    }
}
