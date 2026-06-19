package com.grimreich.core

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EchoSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    private val ECHO_FILE = "eternal_echoes.json"
    private val gson = Gson()
    private val eternalHeroes = mutableListOf<Hero>()

    fun init(context: Context) {
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

    fun recordHero(hero: Hero, context: Context) {
        if (eternalHeroes.none { it.id == hero.id }) {
            eternalHeroes.add(hero)
            save(context)
        }
    }

    private fun save(context: Context) {
        val file = File(context.filesDir, ECHO_FILE)
        file.writeText(gson.toJson(eternalHeroes))
    }

    fun getRandomEcho(): Hero? {
        return if (eternalHeroes.isNotEmpty()) eternalHeroes.random() else null
    }
}
