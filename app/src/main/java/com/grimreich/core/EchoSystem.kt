package com.grimreich.core

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EchoSystem @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gameRepository: GameRepository
) {
    companion object {
        private const val MAX_ETERNAL_HEROES = 15
    }

    private val ECHO_FILE = "eternal_echoes.json"
    private val gson = Gson()
    
    // Defensive measure: Ensure transient if Hero ever gains non-serializable fields
    @Transient
    private val eternalHeroes = mutableListOf<Hero>()

    init {
        loadEchoes()
    }

    private fun loadEchoes() {
        val file = File(context.filesDir, ECHO_FILE)
        if (file.exists()) {
            try {
                val json = file.readText()
                val type = object : TypeToken<MutableList<Hero>>() {}.type
                val loaded: MutableList<Hero> = gson.fromJson(json, type)
                eternalHeroes.clear()
                eternalHeroes.addAll(loaded)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Kept for backward compatibility but made no-op
    fun init(context: Context) {}

    fun recordHero(hero: Hero, context: Context) {
        if (eternalHeroes.none { it.id == hero.id }) {
            eternalHeroes.add(hero)
            // Limit the list size to prevent memory bloat (BUG-R4-11)
            if (eternalHeroes.size > MAX_ETERNAL_HEROES) {
                eternalHeroes.removeAt(0)
            }
            save(context)
        }
    }

    private fun save(context: Context) {
        try {
            val file = File(context.filesDir, ECHO_FILE)
            file.writeText(gson.toJson(eternalHeroes))
        } catch (e: Exception) {
            // Guard against IO errors (Round 4 Audit)
            e.printStackTrace()
        }
    }

    fun getRandomEcho(): Hero? {
        return if (eternalHeroes.isNotEmpty()) eternalHeroes.random() else null
    }
}
