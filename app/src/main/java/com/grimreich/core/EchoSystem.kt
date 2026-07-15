package com.grimreich.core

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.grimreich.systems.WorldStabilitySystem
import com.grimreich.world.ItemCatalogue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EchoSystem @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val gameRepository: GameRepository,
    private val worldStabilitySystem: WorldStabilitySystem,
    private val itemCatalogue: ItemCatalogue
) {
    companion object {
        private const val MAX_ETERNAL_HEROES = 15
    }

    private val ECHO_FILE = "eternal_echoes.json"
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true
    }
    
    @Transient
    private val eternalHeroes = mutableListOf<Hero>()

    init {
        // BUG-03: Moved to loadEchoesAsync to prevent blocking main thread during Hilt init
    }

    suspend fun loadEchoesAsync() = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val file = File(context.filesDir, ECHO_FILE)
        if (file.exists()) {
            try {
                val content = file.readText()
                val dtos = json.decodeFromString<List<HeroDto>>(content)
                eternalHeroes.clear()
                eternalHeroes.addAll(dtos.map { it.toDomain() })
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
            val tempFile = File(context.filesDir, "$ECHO_FILE.tmp")
            
            val dtos = eternalHeroes.map { it.toDto() }
            val content = json.encodeToString(dtos)
            
            // ATOMIC WRITE: Write to temp file then rename
            FileOutputStream(tempFile).use { fos ->
                fos.write(content.toByteArray())
                fos.flush()
            }
            
            if (tempFile.exists()) {
                if (file.exists()) file.delete()
                tempFile.renameTo(file)
            }
        } catch (e: Exception) {
            // Guard against IO errors (Round 4 Audit)
            e.printStackTrace()
        }
    }

    fun getRandomEcho(): Hero? {
        return if (eternalHeroes.isNotEmpty()) eternalHeroes.random() else null
    }

    /**
     * Links a new hero to an eternal echo for inheritance.
     */
    fun linkToEcho(hero: Hero, echoId: String) {
        val echo = eternalHeroes.find { it.id == echoId } ?: return
        
        // Inheritance logic (Iteration 6)
        val bonusXp = (echo.xp * 0.1f).toInt().coerceAtLeast(100)
        hero.xp += bonusXp
        hero.corruption += 15
        
        // Inherit a random high skill
        val bestSkill = echo.skills.maxByOrNull { it.value }
        if (bestSkill != null) {
            val inheritedValue = (bestSkill.value * 0.5f).toInt().coerceAtLeast(20)
            hero.skills[bestSkill.key] = inheritedValue
        }
        
        android.util.Log.d("EchoSystem", "Hero ${hero.name} linked to Echo ${echo.name}. Bonus XP: $bonusXp")
    }

    /**
     * Świadome wywołanie pęknięcia rzeczywistości.
     * Obniża stabilność, ale generuje rzadki surowiec.
     */
    fun forceRealityLeak(regionId: String): String {
        var result = ""
        val state = gameRepository.currentState()
        
        if (state.world.locationId != regionId) {
            return "Nie znajdujesz się w tym regionie."
        }

        worldStabilitySystem.changeStability(-20, "Rytuał Wymuszenia Echa")
        
        gameRepository.updateState { s ->
            // MORALITY SYSTEM: Forcing reality leak is a sin
            s.prayer.sins += 1
            s.prayer.normalize()

            itemCatalogue.createInstance("ing_echo_dust")?.let { dust ->
                s.inventory.add(dust)
                result = "Rzeczywistość pęka... Otrzymano ${dust.name}."
                s.logEntries.add("Echo: $result")
            } ?: run {
                result = "Rytuał nie powiódł się - brak esencji w tym miejscu."
            }
        }
        
        return result
    }
}
