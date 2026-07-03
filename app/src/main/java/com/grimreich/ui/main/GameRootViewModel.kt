package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.*
import com.grimreich.systems.AudioEngine
import com.grimreich.systems.CombatSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GameScreenMode {
    MAIN_MENU, PLAYER_IDENTITY, CHARACTER_CREATOR, WORLD_MAP, CITY, COMBAT, TAVERN, TEMPLE, ALCHEMY, EVENTS, HUB, DIALOGUE, INVENTORY, QUESTS, CHRONICLE, RECRUIT, CHAR_DETAIL, MARKET, DEV_MENU, RITUAL, ENDING, EXPEDITION
}

@HiltViewModel
class GameRootViewModel @Inject constructor(
    val gameRepository: GameRepository,
    private val gameBootstrapper: GameBootstrapper,
    private val combatSystem: CombatSystem,
    private val audioEngine: AudioEngine
) : ViewModel() {

    private val _mode = MutableStateFlow(GameScreenMode.MAIN_MENU)
    val mode: StateFlow<GameScreenMode> = _mode.asStateFlow()

    private val _inspectedHeroId = MutableStateFlow<String?>(null)
    val inspectedHero: StateFlow<Hero?> = combine(gameRepository.gameState, _inspectedHeroId) { state, id ->
        id?.let { state.party.find { h -> h.id == it } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    // Temporary storage for character creation
    private var pendingPlayerName: String? = null

    init {
        // Initial music for main menu
        audioEngine.playForRoute("main_menu")
    }

    fun setMode(mode: GameScreenMode) {
        _mode.value = mode
        val route = when (mode) {
            GameScreenMode.MAIN_MENU        -> "main_menu"
            GameScreenMode.PLAYER_IDENTITY,
            GameScreenMode.CHARACTER_CREATOR -> "character_creator"
            GameScreenMode.HUB             -> "hub"
            GameScreenMode.CITY            -> "city"
            GameScreenMode.WORLD_MAP       -> "map"
            GameScreenMode.COMBAT          -> "combat"
            GameScreenMode.TAVERN          -> "tavern"
            GameScreenMode.MARKET          -> "market"
            GameScreenMode.EXPEDITION      -> "expedition"
            GameScreenMode.EVENTS          -> "events"
            GameScreenMode.RITUAL          -> "ritual"
            GameScreenMode.ENDING          -> "ending"
            GameScreenMode.TEMPLE          -> "ritual" // Fallback to ritual music
            GameScreenMode.ALCHEMY         -> "alchemy"
            GameScreenMode.DIALOGUE        -> "dialogue"
            GameScreenMode.QUESTS          -> "hub" // Stay on hub/city music
            GameScreenMode.CHRONICLE       -> "hub"
            GameScreenMode.RECRUIT         -> "tavern"
            GameScreenMode.INVENTORY       -> "hub"
            GameScreenMode.CHAR_DETAIL     -> "hub"
            GameScreenMode.DEV_MENU        -> "main_menu"
        }
        audioEngine.playForRoute(route)
    }

    fun startNewGame() {
        setMode(GameScreenMode.PLAYER_IDENTITY)
    }

    fun setPlayerIdentity(name: String) {
        pendingPlayerName = name
        setMode(GameScreenMode.CHARACTER_CREATOR)
    }

    fun finalizeCharacterCreation(name: String, career: Career, attrs: Map<String, Int>, skills: List<HeroSkill>) {
        viewModelScope.launch {
            gameBootstrapper.bootstrapFreshWorld()

            gameRepository.updateState { state ->
                state.playerName = pendingPlayerName ?: "Wędrowiec"
                state.heroName = name

                // Create the hero object from creation data
                val hero = Hero(
                    id = "hero_main",
                    name = name,
                    age = 20, // default
                    currentCareer = career,
                    strength = attrs["Str"] ?: 10,
                    agility = attrs["Agi"] ?: 10,
                    perception = attrs["Per"] ?: 10,
                    intelligence = attrs["Int"] ?: 10,
                    endurance = attrs["End"] ?: 10,
                    charisma = attrs["Cha"] ?: 10,
                    piety = attrs["Pie"] ?: 10,
                    hp = 40,
                    maxHp = 40
                )

                // Apply skills
                skills.forEach { hero.skills[it.displayName] = 40 }

                state.party.add(hero)
                state.activeHeroId = hero.id
                state.logEntries.add("Bohater $name wyrusza w drogę.")
            }

            gameRepository.persistCurrentState()
            setMode(GameScreenMode.HUB)
        }
    }

    fun restoreSessionIfValid(): Boolean {
        if (gameRepository.restoreIfAvailable()) {
            setMode(GameScreenMode.HUB)
            return true
        } else {
            gameRepository.log("❌ Błąd wczytywania sesji: Zapis uszkodzony lub nieaktualny.")
            return false
        }
    }

    fun inspectHero(heroId: String) {
        _inspectedHeroId.value = heroId
        setMode(GameScreenMode.CHAR_DETAIL)
    }

    fun upgradeStat(heroId: String, stat: String) {
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            if (hero.attributePoints > 0) {
                val normalizedStat = stat.uppercase()
                var applied = true
                when (normalizedStat) {
                    "STRENGTH", "SIŁA", "STR"           -> hero.strength++
                    "AGILITY", "ZRĘCZNOŚĆ", "AGI"       -> hero.agility++
                    "INTELLIGENCE", "INTELIGENCJA", "INT" -> hero.intelligence++
                    "ENDURANCE", "WYTRZYMAŁOŚĆ", "END"   -> hero.endurance++
                    "PERCEPTION", "PERCEPCJA", "PER"     -> hero.perception++
                    "CHARISMA", "CHARYZMA", "CHA"        -> hero.charisma++
                    "PIETY", "POBOŻNOŚĆ", "PIE"          -> hero.piety++
                    else -> applied = false
                }
                if (applied) {
                    hero.attributePoints--
                    state.logEntries.add("${hero.name} rozwija swoją naturę: $stat +1.")
                }
            }
        }
    }

    fun saveGame() {
        gameRepository.persistCurrentState()
    }
}
