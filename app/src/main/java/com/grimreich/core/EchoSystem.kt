package com.grimreich.core

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * System of Eternal Echoes. 
 * Remembers every hero that ever existed in any playthrough.
 */
object EchoSystem {
    private const val ECHO_FILE = "eternal_echoes.json"
    private val gson = Gson()
    
    // Persistent list of heroes from all past games
    private var eternalHeroes: MutableList<Hero> = mutableListOf()

    /**
     * Loads all historical heroes from the global persistent file.
     */
    fun init(context: Context) {
        val file = File(context.filesDir, ECHO_FILE)
        if (file.exists()) {
            try {
                val json = file.readText()
                val type = object : TypeToken<MutableList<Hero>>() {}.type
                eternalHeroes = gson.fromJson(json, type) ?: mutableListOf()
            } catch (e: Exception) {
                eternalHeroes = mutableListOf()
            }
        }
    }

    /**
     * Call this when a game ends or a hero is "lost" to save them to the echoes.
     */
    fun recordHero(hero: Hero, context: Context) {
        if (eternalHeroes.any { it.id == hero.id }) return
        
        eternalHeroes.add(hero)
        save(context)
    }

    /**
     * Saves the current list of eternal heroes to the file.
     */
    private fun save(context: Context) {
        val file = File(context.filesDir, ECHO_FILE)
        val json = gson.toJson(eternalHeroes)
        file.writeText(json)
    }

    /**
     * Returns a random hero from a previous playthrough to appear as an NPC.
     */
    fun getRandomEcho(): Hero? {
        if (eternalHeroes.isEmpty()) return null
        return eternalHeroes.random()
    }
    
    fun getAllEchoes(): List<Hero> = eternalHeroes
}
